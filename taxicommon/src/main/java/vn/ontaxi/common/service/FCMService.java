package vn.ontaxi.common.service;

import de.bytefish.fcmjava.client.FcmClient;
import de.bytefish.fcmjava.model.options.FcmMessageOptions;
import de.bytefish.fcmjava.model.topics.Topic;
import de.bytefish.fcmjava.requests.notification.NotificationPayload;
import de.bytefish.fcmjava.requests.notification.NotificationUnicastMessage;
import de.bytefish.fcmjava.requests.topic.TopicUnicastMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ontaxi.common.constant.TopicNames;
import vn.ontaxi.common.jpa.entity.Booking;

import java.time.Duration;
import java.util.List;

@Service
public class FCMService {
    private final FcmClient fcmClient;

    @Autowired
    public FCMService(FcmClient fcmClient) {
        this.fcmClient = fcmClient;
    }

    public void postNewTaxiOrder(Booking booking, List<String> fcmTokens) {
        FcmMessageOptions options = FcmMessageOptions.builder().setTimeToLive(Duration.ofHours(1)).build();

        fcmClient.send(new TopicUnicastMessage(options, new Topic(TopicNames.NEW_ORDER), booking));

        for (String fcmToken : fcmTokens) {
            NotificationPayload payload = NotificationPayload.builder()
                    .setTitle("Hệ Thống OnTaxi")
                    .setBody("OnTaxi gửi bạn 1 đơn hàng mới")
                    .setSound(Boolean.toString(true)).build();
            fcmClient.send(new NotificationUnicastMessage(options, fcmToken, payload));
        }
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
