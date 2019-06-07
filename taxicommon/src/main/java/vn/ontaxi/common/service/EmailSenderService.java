package vn.ontaxi.common.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import vn.ontaxi.common.jpa.entity.Customer;
import vn.ontaxi.common.jpa.entity.EmailScheduler;
import vn.ontaxi.common.jpa.entity.EmailSchedulerVsCustomer;
import vn.ontaxi.common.jpa.repository.EmailSchedulerVsCustomerRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@PropertySource("classpath:config.properties")
public class EmailSenderService {

    private final Environment env;
    private final EmailSchedulerVsCustomerRepository emailSchedulerVsCustomerRepository;

    private String sendgridApiKey;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private VelocityEngine velocityEngine;

    public EmailSenderService(Environment env, EmailSchedulerVsCustomerRepository emailSchedulerVsCustomerRepository, VelocityEngine velocityEngine) {
        this.env = env;
        sendgridApiKey = env.getProperty("sendgrid.key");
        this.emailSchedulerVsCustomerRepository = emailSchedulerVsCustomerRepository;
    }

    public void sendEmail(String subject, Email from, Email to, Content content) throws IOException {
        Mail mail = new Mail(from, subject, to, content);
        sendEmail(mail);
    }

    private void sendEmail(Mail mail) throws IOException {
        SendGrid sg = new SendGrid(sendgridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        sg.api(request);
    }

    public void sendEmailScheduler(EmailScheduler emailScheduler) {
        Map<Long, Mail> lstMails = new HashMap<>();
        String subject = emailScheduler.getSubject();
        Email from = new Email(env.getProperty("ontaxi.mail"));

        Query nativeQuery = entityManager.createNativeQuery(emailScheduler.getCustomerGroup().getSqlContent(), Customer.class);
        List<Customer> resultList = nativeQuery.getResultList();
        Map<Long, EmailSchedulerVsCustomer> emailSchedulerVsCustomers = new HashMap<>();
        for (Customer customer : resultList) {
            if (StringUtils.isNotEmpty(customer.getEmail())) {
                EmailSchedulerVsCustomer emailSchedulerVsCustomer = emailSchedulerVsCustomerRepository.findByCustomerIdAndAndEmailSchedulerId(customer.getId(), emailScheduler.getId());
                boolean sendEmail = true;
                if (emailScheduler.isMultipleTimePerCustomer()) {
                    if (emailSchedulerVsCustomer == null)
                        emailSchedulerVsCustomers.put(customer.getId(),new EmailSchedulerVsCustomer(customer.getId(), emailScheduler.getId()));
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
                    Content content = new Content("text/html", emailScheduler.getEmailTemplate().getEmailContent());
                    Mail mail = new Mail(from, subject, new Email(customer.getEmail()), content);
                    lstMails.put(customer.getId(), mail);
                }
            }
        }

        try {
            for (Long customerId : lstMails.keySet()) {
                sendEmail(lstMails.get(customerId));
                emailSchedulerVsCustomerRepository.save(emailSchedulerVsCustomers.get(customerId));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
