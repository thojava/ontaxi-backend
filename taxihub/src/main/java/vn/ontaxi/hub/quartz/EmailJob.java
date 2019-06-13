package vn.ontaxi.hub.quartz;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.jpa.entity.EmailScheduler;
import vn.ontaxi.common.jpa.repository.EmailScheduleRepository;
import vn.ontaxi.common.service.EmailService;

@Component
public class EmailJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(EmailJob.class);

    @Autowired
    EmailScheduleRepository emailScheduleRepository;

    @Autowired
    EmailService emailService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());

        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        Long emailSchedulerId = jobDataMap.getLong("emailSchedulerId");
        EmailScheduler emailScheduler = emailScheduleRepository.findOne(emailSchedulerId);
        if (emailScheduler != null)
            emailService.sendEmailScheduler(emailScheduler);

    }

}
