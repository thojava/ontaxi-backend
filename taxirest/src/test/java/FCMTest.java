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
import vn.ontaxi.common.service.FCMService;
import vn.ontaxi.rest.app.Application;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

@SpringBootTest(classes = {FCMClientFactory.class, FCMService.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class FCMTest {
    @Autowired
    private FcmClient fcmClient;

    @Autowired
    private FCMService fcmService;

    @Test
    public void sendNotificationMessage() {
        FcmMessageOptions options = FcmMessageOptions.builder().setTimeToLive(Duration.ofHours(1)).build();
        NotificationPayload payload = NotificationPayload.builder().setTitle("Title").setBody("Body").build();
        String fcmToken = "c7maUW5qYGM:APA91bEzrM76Xcui0JNzDdetV-0I6G0GdP9VqHSol994jJsGx6fnCkTnhK2TnTiVdYXZ8Wgyohusg9iEE-bC0PoUiL3VtfTeQ7VmmM41G1VOu5x0sURrmZ_5yc5UQ-7ABa9zd-HVVaE2";
        FcmMessageResponse response = fcmClient.send(new NotificationUnicastMessage(options, fcmToken, payload));
        System.out.println(response);
    }

    @Test
    public void sendBookingFCM() {
        Booking booking = new Booking();
        booking.setId(new Random().nextLong());
        booking.setFrom_location("Hà Nội");
        booking.setTo_location("Hà Nam");
        booking.setDeparture_time(new Date());
        String fcmToken = "ftu9h8t2XF0:APA91bFsBvy9A18ymz6LIwx4-6HteY0-FnrT0u-h80Dg0d4RRhFWXhvRP-TgkaWrZo_drEjiePLJ9Q7d2u6HDkgQcrl9SGzsGvTT_gIT2ZSfJtiuw6Jz-YYFVs7a3oBi6rdVRy6C5QkP";
        fcmService.postNewTaxiOrder(booking, Collections.singletonList(fcmToken));
    }
}
