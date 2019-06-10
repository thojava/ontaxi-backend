package vn.ontaxi.common.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.apache.commons.lang3.StringUtils;
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

    public EmailSenderService(Environment env, EmailSchedulerVsCustomerRepository emailSchedulerVsCustomerRepository) {
        this.env = env;
        sendgridApiKey = env.getProperty("sendgrid.key");
        this.emailSchedulerVsCustomerRepository = emailSchedulerVsCustomerRepository;
    }

    public void sendEmail(String subject, String to, String content) throws IOException {
        Email from = new Email(env.getProperty("ontaxi.mail"));
        Content htmlContent = new Content("text/html", content);

        sendEmail(subject, from, new Email(to), htmlContent);
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
        Response api = sg.api(request);
        System.out.println();
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
                    String emailContent = vn.ontaxi.common.utils.StringUtils.fillRegexParams(emailScheduler.getEmailTemplate().getEmailContent(), new HashMap<String, String>() {{
                        put("\\$\\{name\\}", customer.getName());
                    }});
                    Content content = new Content("text/html", emailContent);
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
