package vn.ontaxi.hub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import vn.ontaxi.common.jpa.entity.EmailScheduler;
import vn.ontaxi.common.jpa.repository.EmailScheduleRepository;
import vn.ontaxi.common.service.EmailSenderService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Service
public class EmailSchedulerService {

    private Map<String, ScheduledFuture<?>> scheduledFutures = new HashMap<>();

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private ThreadPoolTaskScheduler scheduler;

    @Autowired
    private EmailScheduleRepository emailScheduleRepository;

    public void scheduleEmail(EmailScheduler emailScheduler) {
        Date startDate = new Date();
        if (emailScheduler.isEnable() && (emailScheduler.getEndTime() == null || startDate.before(emailScheduler.getEndTime()))) {
            stopAndRemoveSchedule(emailScheduler.getKey());
            if (startDate.before(emailScheduler.getStartTime()))
                startDate = emailScheduler.getStartTime();

            scheduler.schedule(() -> {
                scheduledFutures.put(emailScheduler.getKey(), start(() -> emailSenderService.sendEmailScheduler(emailScheduler), "0 1/3 * * * *"));
            }, startDate);

            if (emailScheduler.getEndTime() != null) {
                scheduledFutures.put(emailScheduler.getKey() + "endTime", scheduler.schedule(() -> {
                    stopAndRemoveSchedule(emailScheduler.getKey());
                    emailScheduler.setEnable(false);
                    emailScheduleRepository.save(emailScheduler);
                }, emailScheduler.getEndTime()));
            }
        }
    }

    public void stopAndRemoveSchedule(String key) {
        ScheduledFuture<?> scheduledFuture = scheduledFutures.get(key);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            scheduledFutures.remove(key);
        }

        ScheduledFuture<?> scheduledFutureEndTime = scheduledFutures.get(key + "endTime");
        if (scheduledFutureEndTime != null) {
            scheduledFutureEndTime.cancel(true);
            scheduledFutures.remove(key + "endTime");
        }
    }

    public ScheduledFuture<?> start(Runnable task, String scheduleExpression) {
        return scheduler.schedule(task, new CronTrigger(scheduleExpression));
    }

}
