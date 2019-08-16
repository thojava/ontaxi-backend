package vn.ontaxi.hub.quartz;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.jpa.entity.EmailScheduler;
import vn.ontaxi.common.jpa.repository.EmailScheduleRepository;
import vn.ontaxi.hub.service.EmailSchedulerService;

@Component
public class EmailJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(EmailJob.class);

    private final EmailScheduleRepository emailScheduleRepository;

    private final EmailSchedulerService emailSchedulerService;

    public EmailJob(EmailScheduleRepository emailScheduleRepository, EmailSchedulerService emailSchedulerService) {
        this.emailScheduleRepository = emailScheduleRepository;
        this.emailSchedulerService = emailSchedulerService;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());

        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        Long emailSchedulerId = jobDataMap.getLong("emailSchedulerId");
        EmailScheduler emailScheduler = emailScheduleRepository.findById(emailSchedulerId).get();
        if (emailScheduler != null)
            emailSchedulerService.sendEmailScheduler(emailScheduler);

    }
}
