package vn.ontaxi.common.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@PropertySource("classpath:config.properties")
public class EmailService {
    private Logger logger = Logger.getLogger(EmailService.class);

    private final String sendGridApiKey;
    private final Email fromEmail;

    public EmailService(Environment env) {
        sendGridApiKey = env.getProperty("sendgrid.key");
        fromEmail = new Email(env.getProperty("from.mail"), env.getProperty("from.name"));
    }

    public void sendEmail(String subject, String to, String content) {
        Content htmlContent = new Content("text/html", content);
        Mail mail = new Mail(fromEmail, subject, new Email(to), htmlContent);
        try {
            sendEmail(mail);
        } catch (IOException e) {
            logger.error("Error when sending mail " + mail.getSubject()
                    + " to " + mail.getPersonalization().get(0).getTos().get(0).getEmail(), e);
        }
    }

    private void sendEmail(Mail mail) throws IOException {
        SendGrid sendGrid = new SendGrid(sendGridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        String emailContent = mail.build();
        request.setBody(emailContent);

        Response response = sendGrid.api(request);
        logger.debug("Send mail " + mail.getSubject()
                + " to " + mail.getPersonalization().get(0).getTos().get(0).getEmail()
                + " with status " + response.getStatusCode() + " " + response.getBody());
    }
}
