package vn.ontaxi.common.service;

import de.bytefish.fcmjava.client.FcmClient;
import de.bytefish.fcmjava.http.options.IFcmClientSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:config.properties")
public class FCMClientFactory {
    @Autowired
    private Environment env;

    @Bean
    public FcmClient buildFcmClient() {
        final IFcmClientSettings fcmClientSettings = new IFcmClientSettings() {
            @Override
            public String getFcmUrl() {
                return env.getProperty("fcm_url");
            }

            @Override
            public String getApiKey() {
                return env.getProperty("api_key");
            }
        };
        
        return new FcmClient(fcmClientSettings);
    }
}
