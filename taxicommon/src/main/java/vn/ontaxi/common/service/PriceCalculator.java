package vn.ontaxi.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ontaxi.common.constant.CarTypes;
import vn.ontaxi.common.jpa.entity.Booking;
import vn.ontaxi.common.jpa.entity.PriceConfiguration;
import vn.ontaxi.common.jpa.repository.PriceConfigurationRepository;
import vn.ontaxi.common.model.PriceInfo;
import vn.ontaxi.common.utils.DateUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

import static vn.ontaxi.common.utils.PriceUtils.calculatePrice;

@Service
public class PriceCalculator {
    private final PriceConfigurationRepository priceConfigurationRepository;

    @Autowired
    public PriceCalculator(PriceConfigurationRepository priceConfigurationRepository) {
        this.priceConfigurationRepository = priceConfigurationRepository;
    }

    public void calculateEstimatedPrice(Booking booking) {
        double estimatedWaitHours = 0.d;
        if(booking.isRoundTrip()) {
            double estimatedTripHours = booking.getTotal_distance() / 60;
            LocalDate d1 = booking.getDeparture_time().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate d2 = booking.getReturnDepartureTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            estimatedWaitHours = ChronoUnit.HOURS.between(d1, d2) - estimatedTripHours;
        }

        PriceInfo priceInfo = calculatePrice(booking.getUnit_price(), booking.getTotal_distance(), booking.getTotal_distance(), booking.getCar_type(),
                booking.isRoundTrip(), estimatedWaitHours, 0, booking.getPromotionPercentage());
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
        } else if(CarTypes.N16 == car_type) {
            return priceConfiguration.getNormal_16();
        }

        throw new IllegalArgumentException("Invalid type " + car_type);
    }
}
