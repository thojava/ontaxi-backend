package vn.ontaxi.common.service;

import org.apache.commons.lang3.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

@Service
public class SMSService {
    private static final Logger logger = LoggerFactory.getLogger(SMSService.class);
    private final Environment env;
    private String restBaseURL;

    @Autowired
    public SMSService(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void init() {
        restBaseURL = String.format("http://rest.esms.vn/MainService.svc/json/SendMultipleMessage_V4_get?ApiKey=%s&SecretKey=%s&SmsType=%d",
                env.getProperty("sms_api_key"), env.getProperty("sms_secret_key"), 8);

    }

    public void sendSMS(String phoneNumber, String content) {
        try {
            String fullRestURL = restBaseURL + "&Phone=" + phoneNumber + "&Content=" + URLEncoder.encode(content, CharEncoding.UTF_8);
            logger.debug("smsurl " + fullRestURL);
            new URL(fullRestURL).openConnection().getContent();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
