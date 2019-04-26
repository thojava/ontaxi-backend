package vn.ontaxi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import vn.ontaxi.constant.BooleanConstants;
import vn.ontaxi.constant.CarTypes;
import vn.ontaxi.jpa.entity.Booking;
import vn.ontaxi.jpa.entity.PriceConfiguration;
import vn.ontaxi.jpa.repository.PriceConfigurationRepository;
import vn.ontaxi.service.PriceCalculator;
import vn.ontaxi.service.SMSService;
import vn.ontaxi.utils.PriceUtils;
import vn.ontaxi.utils.SMSContentBuilder;

@SpringBootTest(classes = {SMSService.class, PriceCalculator.class, PriceConfigurationRepository.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase
public class SMSServiceTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private SMSService smsService;
    @Autowired
    private PriceCalculator priceCalculator;
    private static final String TEST_PHONE_NUMBER = "0976857832";

    public SMSServiceTest() {
    }

    @Before
    public void init() {
        PriceConfiguration priceConfiguration = new PriceConfiguration();
        priceConfiguration.setGood_4(8500);
        priceConfiguration.setNormal_7(10500);
        entityManager.persist(priceConfiguration);
        entityManager.flush();
    }

    private Booking createSingleRoundBooking() {
        Booking singleRouteBooking = new Booking();
        singleRouteBooking.setIs_round_trip(BooleanConstants.NO);

        singleRouteBooking.setMobile(TEST_PHONE_NUMBER);
        singleRouteBooking.setFrom_city("Ha Noi");
        singleRouteBooking.setTo_city("Ha Nam");
        singleRouteBooking.setActual_total_distance(80);
        singleRouteBooking.setCar_type(CarTypes.GOOD_4);
        singleRouteBooking.setTransport_fee(50000);
        singleRouteBooking.setActualTotalPriceBeforePromotion(700000);
        singleRouteBooking.setPromotionPercentage(5);
        singleRouteBooking.setActual_total_price(650000);
        singleRouteBooking.setUnit_price(priceCalculator.getPricePerKm(singleRouteBooking.getCar_type()));

        PriceUtils.calculateActualPrice(singleRouteBooking);

        return singleRouteBooking;
    }

    private Booking createReturnedRoundBooking() {
        Booking returnedRounded = new Booking();
        returnedRounded.setIs_round_trip(BooleanConstants.YES);

        returnedRounded.setMobile(TEST_PHONE_NUMBER);
        returnedRounded.setFrom_city("Ha Noi");
        returnedRounded.setTo_city("Ha Nam");
        returnedRounded.setOutward_distance(100);
        returnedRounded.setReturn_distance(90);
        returnedRounded.setCar_type(CarTypes.GOOD_4);
        returnedRounded.setWait_hours(5);
        returnedRounded.setPromotionPercentage(5);
        returnedRounded.setTransport_fee(50000);
        returnedRounded.setUnit_price(priceCalculator.getPricePerKm(returnedRounded.getCar_type()));
        PriceUtils.calculateActualPrice(returnedRounded);

        return returnedRounded;
    }

    @Test
    public void sendSingleRouteSMS() {
        Booking singleRoundBooking = createSingleRoundBooking();
        smsService.sendSMS(singleRoundBooking.getMobile(), SMSContentBuilder.buildCompleteOrderSMSContent(singleRoundBooking));
    }

    @Test
    public void sendReturnRouteSMS() {
        Booking returnedRoundBooking = createReturnedRoundBooking();
        smsService.sendSMS(returnedRoundBooking.getMobile(), SMSContentBuilder.buildCompleteOrderSMSContent(returnedRoundBooking));
    }
}
