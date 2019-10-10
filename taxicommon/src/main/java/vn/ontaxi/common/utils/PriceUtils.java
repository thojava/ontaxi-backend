package vn.ontaxi.common.utils;

import org.springframework.stereotype.Service;
import vn.ontaxi.common.constant.CarTypes;
import vn.ontaxi.common.jpa.entity.Booking;
import vn.ontaxi.common.model.PriceInfo;
import vn.ontaxi.common.service.DistanceMatrixService;

@Service
public class PriceUtils {
    private final DistanceMatrixService distanceMatrixService;

    private static final String TAM_DAO_LOCATION = "Tam Dao, Tam Đảo, Vĩnh Phúc";

    public PriceUtils(DistanceMatrixService distanceMatrixService) {
        this.distanceMatrixService = distanceMatrixService;
    }

    public PriceInfo calculatePrice(String toLocation, double pricePerKm, double outwardDistant, double returnDistant, CarTypes car_type, boolean isRoundTrip,
                                           double wait_hours, double transportFee, double promotionPercentage) {
        double terrain_price = 0.D;
        if(distanceMatrixService.getDistance(toLocation, TAM_DAO_LOCATION) < 4000) {
            terrain_price = 50000;
        }

        if (isRoundTrip) {
            double lowDistance = Math.min(outwardDistant, returnDistant);
            double highDistance = Math.max(outwardDistant, returnDistant);

            double outwardPrice = highDistance * pricePerKm;

            double returnPrice = lowDistance * pricePerKm * getReturnRoundPercentage(lowDistance) / 100;

            double waitPrice = wait_hours * getPricePerWaitHour(car_type);

            return new PriceInfo(outwardPrice, returnPrice, waitPrice, terrain_price * 2, transportFee, promotionPercentage);
        } else {
            return new PriceInfo(outwardDistant * pricePerKm, 0, 0, terrain_price, transportFee, promotionPercentage);
        }
    }

    public static double getFreeWaitTime(double distance) {
        // 1 km get 1.5 min free wait.
        return distance * 1.5 / 60;
    }

    public static int getReturnRoundPercentage(double distance) {
        return 40;
    }

    public static int getPricePerWaitHour(CarTypes car_type) {
        return 40000;
    }

    public void calculateActualPrice(Booking booking) {
        PriceInfo priceInfo = calculatePrice(booking.getTo_location(), booking.getUnit_price(), booking.isRoundTrip() ? booking.getOutward_distance() : booking.getActual_total_distance(), booking.getReturn_distance(),
                booking.getCar_type(), booking.isRoundTrip(), booking.getWait_hours(), booking.getTransport_fee(), booking.getPromotionPercentage());
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
