package vn.ontaxi.service;

import de.bytefish.fcmjava.client.FcmClient;
import de.bytefish.fcmjava.model.options.FcmMessageOptions;
import de.bytefish.fcmjava.model.topics.Topic;
import de.bytefish.fcmjava.requests.topic.TopicUnicastMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ontaxi.constant.TopicNames;
import vn.ontaxi.jpa.entity.Booking;

import java.time.Duration;

@Service
public class FCMService {
    private final FcmClient fcmClient;

    @Autowired
    public FCMService(FcmClient fcmClient) {
        this.fcmClient = fcmClient;
    }

    public void postNewTaxiOrder(Booking booking) {
        FcmMessageOptions options = FcmMessageOptions.builder().setTimeToLive(Duration.ofHours(1)).build();

        fcmClient.send(new TopicUnicastMessage(options, new Topic(TopicNames.NEW_ORDER), booking));
    }

    public void abortNewTaxiOrder(Booking booking) {
        FcmMessageOptions options = FcmMessageOptions.builder().setTimeToLive(Duration.ofHours(1)).build();

        fcmClient.send(new TopicUnicastMessage(options, new Topic(TopicNames.ABORT_ORDER), booking));
    }

    public void postAcceptedTaxiOrder(Booking booking) {
        FcmMessageOptions options = FcmMessageOptions.builder().setTimeToLive(Duration.ofHours(1)).build();

        fcmClient.send(new TopicUnicastMessage(options, new Topic(TopicNames.ACCEPTED_ORDER), booking));
    }
}
