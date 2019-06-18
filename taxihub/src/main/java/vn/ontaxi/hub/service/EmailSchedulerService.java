package vn.ontaxi.hub.service;

import com.sendgrid.helpers.mail.Mail;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ontaxi.common.jpa.entity.Customer;
import vn.ontaxi.common.jpa.entity.EmailScheduler;
import vn.ontaxi.common.jpa.entity.EmailSchedulerVsCustomer;
import vn.ontaxi.common.jpa.repository.EmailSchedulerVsCustomerRepository;
import vn.ontaxi.common.service.EmailService;
import vn.ontaxi.hub.quartz.EmailJob;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailSchedulerService {
    @PersistenceContext
    private EntityManager entityManager;

    private final EmailSchedulerVsCustomerRepository emailSchedulerVsCustomerRepository;
    private final EmailService emailService;
    private final Scheduler scheduler;

    private final String JOB_GROUP = "email-jobs";
    private final String TRIGGER_GROUP = "email-triggers";

    @Autowired
    public EmailSchedulerService(EmailSchedulerVsCustomerRepository emailSchedulerVsCustomerRepository, EmailService emailService, Scheduler scheduler) {
        this.emailSchedulerVsCustomerRepository = emailSchedulerVsCustomerRepository;
        this.emailService = emailService;
        this.scheduler = scheduler;
    }

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

    public void sendEmailScheduler(EmailScheduler emailScheduler) {
        String subject = emailScheduler.getEmailTemplate().getSubject();

        Query nativeQuery = entityManager.createNativeQuery(emailScheduler.getCustomerGroup().getSqlContent(), Customer.class);
        List<Customer> resultList = nativeQuery.getResultList();
        Map<Long, EmailSchedulerVsCustomer> emailSchedulerVsCustomers = new HashMap<>();
        for (Customer customer : resultList) {
            if (StringUtils.isNotEmpty(customer.getEmail())) {
                EmailSchedulerVsCustomer emailSchedulerVsCustomer = emailSchedulerVsCustomerRepository.findByCustomerIdAndAndEmailSchedulerId(customer.getId(), emailScheduler.getId());
                boolean sendEmail = true;
                if (emailScheduler.isMultipleTimePerCustomer()) {
                    if (emailSchedulerVsCustomer == null)
                        emailSchedulerVsCustomers.put(customer.getId(), new EmailSchedulerVsCustomer(customer.getId(), emailScheduler.getId()));
                    else {
                        emailSchedulerVsCustomer.setCount(emailSchedulerVsCustomer.getCount() + 1);
                        emailSchedulerVsCustomers.put(customer.getId(), emailSchedulerVsCustomer);
                    }

                } else {
                    if (emailSchedulerVsCustomer == null) {
                        emailSchedulerVsCustomers.put(customer.getId(), new EmailSchedulerVsCustomer(customer.getId(), emailScheduler.getId()));
                    } else sendEmail = false;
                }

                if (sendEmail) {
                    String emailContent = vn.ontaxi.common.utils.StringUtils.fillRegexParams(emailScheduler.getEmailTemplate().getEmailContent(), new HashMap<String, String>() {{
                        put("\\$\\{name\\}", customer.getName());
                    }});

                    emailService.sendEmail(subject, customer.getEmail(), emailContent);
                    emailSchedulerVsCustomerRepository.save(emailSchedulerVsCustomers.get(customer.getId()));
                }
            }
        }
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
