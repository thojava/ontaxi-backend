import de.bytefish.fcmjava.client.FcmClient;
import de.bytefish.fcmjava.model.options.FcmMessageOptions;
import de.bytefish.fcmjava.model.topics.Topic;
import de.bytefish.fcmjava.requests.notification.NotificationPayload;
import de.bytefish.fcmjava.requests.notification.NotificationUnicastMessage;
import de.bytefish.fcmjava.requests.topic.TopicUnicastMessage;
import de.bytefish.fcmjava.responses.FcmMessageResponse;
import de.bytefish.fcmjava.responses.TopicMessageResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import vn.ontaxi.common.constant.TopicNames;
import vn.ontaxi.common.jpa.entity.Booking;
import vn.ontaxi.common.service.ConfigurationService;
import vn.ontaxi.common.service.DistanceMatrixService;
import vn.ontaxi.common.service.FCMClientFactory;
import vn.ontaxi.rest.app.Application;

import java.time.Duration;
import java.util.Date;

@SpringBootTest(classes = {FCMClientFactory.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class FCMTest {
    @Autowired
    private FcmClient fcmClient;

    @Test
    public void sendNotificationMessage() {
        FcmMessageOptions options = FcmMessageOptions.builder().setTimeToLive(Duration.ofHours(1)).build();
        NotificationPayload payload = NotificationPayload.builder().setTitle("Title").setBody("Body").build();
        FcmMessageResponse send = fcmClient.send(new NotificationUnicastMessage(options, String.format("/%s/%s", "topics", TopicNames.NEW_ORDER), payload));
        System.out.println(send);
    }

    @Test
    public void sendFirebaseMessage() {
        Booking booking = new Booking();
        booking.setFrom_location("Hà Nội");
        booking.setTo_location("Hà Nam");
        booking.setDeparture_time(new Date());
        FcmMessageOptions options = FcmMessageOptions.builder().setTimeToLive(Duration.ofHours(1)).build();
        TopicMessageResponse fcmMessageResponse = fcmClient.send(new TopicUnicastMessage(options, new Topic(TopicNames.NEW_ORDER), booking));
        System.out.println(fcmMessageResponse);
    }
}
