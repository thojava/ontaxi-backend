package vn.ontaxi.common.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import vn.ontaxi.common.jpa.entity.EmailTemplateHeaderFooter;
import vn.ontaxi.common.jpa.repository.EmailTemplateHeaderFooterRepository;
import vn.ontaxi.common.utils.StringUtils;

import java.io.IOException;
import java.util.List;

@Service
@PropertySource("classpath:config.properties")
public class EmailService {
    private Logger logger = Logger.getLogger(EmailService.class);

    private final String sendGridApiKey;
    private final Email fromEmail;
    private final EmailTemplateHeaderFooterRepository emailTemplateHeaderFooterRepository;

    public EmailService(Environment env, EmailTemplateHeaderFooterRepository emailTemplateHeaderFooterRepository) {
        sendGridApiKey = env.getProperty("sendgrid.key");
        fromEmail = new Email(env.getProperty("from.mail"), env.getProperty("from.name"));
        this.emailTemplateHeaderFooterRepository = emailTemplateHeaderFooterRepository;
    }

    public void sendEmail(String subject, String to, String content) {
        Mail mail = new Mail(fromEmail, subject, new Email(to), getHtmlContent(content));
        try {
            sendEmail(mail);
        } catch (IOException e) {
            logger.error("Error when sending mail " + mail.getSubject()
                    + " to " + mail.getPersonalization().get(0).getTos().get(0).getEmail(), e);
        }
    }

    private Content getHtmlContent(String content) {

        List<EmailTemplateHeaderFooter> lstActivations = emailTemplateHeaderFooterRepository.findByActiveTrue();
        if (CollectionUtils.isNotEmpty(lstActivations)) {
            String header = lstActivations.get(0).getHeader();
            if (StringUtils.isNotEmpty(header))
                content = header + "<br/>" + content;
            String footer = lstActivations.get(0).getFooter();
            if (StringUtils.isNotEmpty(footer))
                content = content + "<br/>" + footer;
        }

        return new Content("text/html", content);
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
