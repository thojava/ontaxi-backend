package vn.ontaxi.common.utils;

import org.springframework.stereotype.Service;
import vn.ontaxi.common.constant.CarType;
import vn.ontaxi.common.jpa.entity.Booking;
import vn.ontaxi.common.jpa.entity.PriceConfiguration;
import vn.ontaxi.common.jpa.repository.PriceConfigurationRepository;
import vn.ontaxi.common.model.PriceInfo;
import vn.ontaxi.common.service.DistanceMatrixService;

@Service
public class PriceUtils {
    private static final String TAM_DAO_LOCATION = "Tam Dao, Tam Đảo, Vĩnh Phúc";

    private final DistanceMatrixService distanceMatrixService;
    private final PriceConfigurationRepository priceConfigurationRepository;

    public PriceUtils(DistanceMatrixService distanceMatrixService, PriceConfigurationRepository priceConfigurationRepository) {
        this.distanceMatrixService = distanceMatrixService;
        this.priceConfigurationRepository = priceConfigurationRepository;
    }

    public PriceInfo calculatePrice(String fromLocation, String toLocation, double pricePerKm, double outwardDistant, double returnDistant, CarType carType, boolean isRoundTrip,
                                    double wait_hours, boolean driverWillWait, double transportFee, double promotionPercentage) {
        double terrain_price = calculateTerrainPrice(fromLocation, toLocation);

        if (isRoundTrip) {
            double lowDistance = Math.min(outwardDistant, returnDistant);
            double highDistance = Math.max(outwardDistant, returnDistant);

            double outwardPrice = highDistance * pricePerKm;

            double returnPrice = lowDistance * pricePerKm * getReturnRoundPercentage(driverWillWait) / 100;

            double waitPrice = driverWillWait ? Math.max(wait_hours - PriceUtils.getFreeWaitTime(highDistance), 0) * getPricePerWaitHour(carType) : 0;

            return new PriceInfo(outwardPrice, returnPrice, waitPrice, terrain_price * 2, transportFee, promotionPercentage);
        } else {
            return new PriceInfo(outwardDistant * pricePerKm, 0, 0, terrain_price, transportFee, promotionPercentage);
        }
    }

    private double calculateTerrainPrice(String fromLocation, String toLocation) {
        double terrain_price = 0.D;
        if(distanceMatrixService.getDistance(toLocation, TAM_DAO_LOCATION) < 4000 || distanceMatrixService.getDistance(fromLocation, TAM_DAO_LOCATION) < 4000) {
            terrain_price = 50000;
        }
        return terrain_price;
    }
    public static double getFreeWaitTime(double distance) {
        // 1 km get 1.5 min free wait.
        return distance * 1.5 / 60;
    }

    public double getReturnRoundPercentage(boolean driverWillWait) {
        PriceConfiguration priceConfiguration = priceConfigurationRepository.findAll().get(0);
        return driverWillWait ? priceConfiguration.getReturn_round_percentage() : priceConfiguration.getReturn_round_percentage_without_waiting();
    }

    public static int getPricePerWaitHour(CarType car_type) {
        return 40000;
    }

    public void calculateActualPrice(Booking booking) {
        PriceInfo priceInfo = calculatePrice(booking.getFrom_location(), booking.getTo_location(), booking.getUnit_price(), booking.isRoundTrip() ? booking.getOutward_distance() : booking.getActual_total_distance(), booking.getReturn_distance(),
                booking.getCar_type(), booking.isRoundTrip(), booking.getWait_hours(), booking.isDriver_will_wait(), booking.getTransport_fee(), booking.getPromotionPercentage());
        booking.setActual_total_price(priceInfo.getTotal_price());
        booking.setActualTotalPriceBeforePromotion(priceInfo.getTotal_price_before_promotion());
        booking.setActual_outward_price(priceInfo.outwardPrice);
        booking.setActual_return_price(priceInfo.returnPrice);
        booking.setActual_wait_price(priceInfo.waitPrice);
        booking.setActual_terrain_price(priceInfo.terrainPrice);
    }

    public double calculateDriverFee(double priceWithoutTransportFee, double fee_percentage, double promotion_percentage) {
        return priceWithoutTransportFee * (fee_percentage - promotion_percentage) / 100;
    }
}
