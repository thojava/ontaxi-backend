package vn.ontaxi.hub.service;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ontaxi.common.jpa.entity.EmailScheduler;
import vn.ontaxi.hub.quartz.EmailJob;

import java.util.Date;

@Service
public class EmailSchedulerService {

    @Autowired
    private Scheduler scheduler;

    private final String JOB_GROUP = "email-jobs";
    private final String TRIGGER_GROUP = "email-triggers";

    public void addOrUpdateEmailScheduler(EmailScheduler emailScheduler) throws SchedulerException {
        if (emailScheduler.isEnable() && (emailScheduler.getEndTime() == null || (emailScheduler.getEndTime() != null && emailScheduler.getEndTime().after(new Date())))) {
            JobDetail jobDetail = buildJobDetail(emailScheduler);
            Trigger trigger = buildJobTrigger(jobDetail, emailScheduler);
            if (scheduler.checkExists(trigger.getKey()))
                scheduler.rescheduleJob(trigger.getKey(), trigger);
            else if (scheduler.checkExists(trigger.getJobKey())) {
                scheduler.scheduleJob(trigger);
            } else
                scheduler.scheduleJob(jobDetail, trigger);
        }

    }

    public void pauseEmailScheduler(EmailScheduler emailScheduler) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(emailScheduler.getKey(), TRIGGER_GROUP);
        if (scheduler.checkExists(triggerKey)){
            scheduler.unscheduleJob(triggerKey);
        }
    }

    public void resumeEmailScheduler(EmailScheduler emailScheduler) throws SchedulerException {
        addOrUpdateEmailScheduler(emailScheduler);
    }

    public void deleteEmailScheduler(EmailScheduler emailScheduler) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(emailScheduler.getKey(), TRIGGER_GROUP);
        if (scheduler.checkExists(triggerKey)){
            scheduler.unscheduleJob(triggerKey);
        }
        JobKey jobKey = new JobKey(emailScheduler.getKey(), JOB_GROUP);
        if (scheduler.checkExists(jobKey))
            scheduler.deleteJob(jobKey);
    }

    private JobDetail buildJobDetail(EmailScheduler emailScheduler) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("emailSchedulerId", emailScheduler.getId());

        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(emailScheduler.getKey(), JOB_GROUP)
                .withDescription("Send Email Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, EmailScheduler emailScheduler) {

        Date startAt = new Date();
        if (emailScheduler.getStartTime().after(startAt))
            startAt = emailScheduler.getStartTime();

        TriggerBuilder<CronTrigger> emailTrigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), TRIGGER_GROUP)
                .withDescription("Send Email Trigger")
                .startAt(startAt)
                .withSchedule(CronScheduleBuilder.cronSchedule(emailScheduler.getCronJob()));

        if (emailScheduler.getEndTime() != null)
            emailTrigger.endAt(emailScheduler.getEndTime());

        return emailTrigger.build();

    }

}
