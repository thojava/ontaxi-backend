package vn.ontaxi.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ontaxi.common.constant.CarTypes;
import vn.ontaxi.common.jpa.entity.Booking;
import vn.ontaxi.common.jpa.entity.PriceConfiguration;
import vn.ontaxi.common.jpa.repository.PriceConfigurationRepository;
import vn.ontaxi.common.model.PriceInfo;

import static vn.ontaxi.common.utils.PriceUtils.calculatePrice;

@Service
public class PriceCalculator {
    private final PriceConfigurationRepository priceConfigurationRepository;

    @Autowired
    public PriceCalculator(PriceConfigurationRepository priceConfigurationRepository) {
        this.priceConfigurationRepository = priceConfigurationRepository;
    }

    public void calculateEstimatedPrice(Booking booking) {
        PriceInfo priceInfo = calculatePrice(booking.getUnit_price(), booking.getTotal_distance(), booking.getTotal_distance(), booking.getCar_type(),
                booking.isRoundTrip(), booking.getWait_hours(), 0, booking.getPromotionPercentage());
        booking.setTotal_price(priceInfo.getTotal_price());
        booking.setTotalPriceBeforePromotion(priceInfo.getTotal_price_before_promotion());
        booking.setOutward_price(priceInfo.outwardPrice);
        booking.setReturn_price(priceInfo.returnPrice);
        booking.setWait_price(priceInfo.waitPrice);
    }







    public double getPricePerKm(CarTypes car_type) {
        PriceConfiguration priceConfiguration = priceConfigurationRepository.findAll().get(0);
        if (CarTypes.N4 == car_type) {
            return priceConfiguration.getNormal_4();
        } else if (CarTypes.G4 == car_type) {
            return priceConfiguration.getGood_4();
        } else if (CarTypes.N7 == car_type) {
            return priceConfiguration.getNormal_7();
        }

        throw new IllegalArgumentException("Invalid type " + car_type);
    }





//    public static void main(String[] args) {
//        FacebookClient facebookClient = new DefaultFacebookClient(MY_ACCESS_TOKEN, Version.LATEST);

////        System.out.println(getFreeWaitTime(75));
////        System.out.println(calculatePrice(80, 80, CarTypes.GOOD_4, false, 0));
////        System.out.println(getFreeWaitTime(170));
//        System.out.println(calculatePrice(132, 170, CarTypes.GOOD_4, true, 10, 0));
////        System.out.println(132 * 8500 * 0.2 + 170 * 8500 + 230000);
//    }
}
