package vn.ontaxi.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ontaxi.common.constant.CarType;
import vn.ontaxi.common.jpa.entity.Booking;
import vn.ontaxi.common.jpa.entity.PriceConfiguration;
import vn.ontaxi.common.jpa.repository.PriceConfigurationRepository;
import vn.ontaxi.common.model.PriceInfo;
import vn.ontaxi.common.utils.PriceUtils;

import java.util.concurrent.TimeUnit;

@Service
public class PriceCalculator {
    private final PriceConfigurationRepository priceConfigurationRepository;
    private final PriceUtils priceUtils;

    @Autowired
    public PriceCalculator(PriceConfigurationRepository priceConfigurationRepository, PriceUtils priceUtils) {
        this.priceConfigurationRepository = priceConfigurationRepository;
        this.priceUtils = priceUtils;
    }

    public void calculateEstimatedPrice(Booking booking) {
        double estimatedWaitHours = 0.d;
        if(booking.isRoundTrip()) {
            double estimatedTripHours = booking.getTotal_distance() / 60;
            long differenceInMillis = booking.getReturnDepartureTime().getTime() - booking.getDeparture_time().getTime();
            estimatedWaitHours = TimeUnit.SECONDS.toHours(differenceInMillis / 1000) - estimatedTripHours;
        }

        PriceInfo priceInfo = priceUtils.calculatePrice(booking.getFrom_location(), booking.getTo_location(), booking.getUnit_price(), booking.getTotal_distance(), booking.getTotal_distance(), booking.getCar_type(),
                booking.isRoundTrip(), estimatedWaitHours, booking.isDriver_will_wait(), 0, booking.getPromotionPercentage());
        booking.setTotal_price(priceInfo.getTotal_price());
        booking.setTotalPriceBeforePromotion(priceInfo.getTotal_price_before_promotion());
        booking.setOutward_price(priceInfo.outwardPrice);
        booking.setReturn_price(priceInfo.returnPrice);
        booking.setWait_price(priceInfo.waitPrice);
        booking.setTerrain_price(priceInfo.terrainPrice);
        booking.setWait_hours(estimatedWaitHours);
    }


    public double getPricePerKm(CarType car_type) {
        PriceConfiguration priceConfiguration = priceConfigurationRepository.findAll().get(0);
        if (CarType.N4 == car_type) {
            return priceConfiguration.getNormal_4();
        } else if (CarType.G4 == car_type) {
            return priceConfiguration.getGood_4();
        } else if (CarType.N7 == car_type) {
            return priceConfiguration.getNormal_7();
        } else if(CarType.N16 == car_type) {
            return priceConfiguration.getNormal_16();
        }

        throw new IllegalArgumentException("Invalid type " + car_type);
    }
}
