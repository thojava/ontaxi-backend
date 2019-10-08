package vn.ontaxi.common.utils;

import vn.ontaxi.common.constant.CarTypes;
import vn.ontaxi.common.jpa.entity.Booking;
import vn.ontaxi.common.model.PriceInfo;

public class PriceUtils {
    public static PriceInfo calculatePrice(double pricePerKm, double outwardDistant, double returnDistant, CarTypes car_type, boolean isRoundTrip,
                                           double wait_hours, double transportFee, double promotionPercentage) {
        if (isRoundTrip) {
            double lowDistance = Math.min(outwardDistant, returnDistant);
            double highDistance = Math.max(outwardDistant, returnDistant);

            double outwardPrice = highDistance * pricePerKm;

            double returnPrice = lowDistance * pricePerKm * getReturnRoundPercentage(lowDistance) / 100;

            double waitPrice = wait_hours * getPricePerWaitHour(car_type);

            return new PriceInfo(outwardPrice, returnPrice, waitPrice, transportFee, promotionPercentage);
        } else {
            return new PriceInfo(outwardDistant * pricePerKm, 0, 0, transportFee, promotionPercentage);
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

    public static void calculateActualPrice(Booking booking) {
        PriceInfo priceInfo = calculatePrice(booking.getUnit_price(), booking.isRoundTrip() ? booking.getOutward_distance() : booking.getActual_total_distance(), booking.getReturn_distance(),
                booking.getCar_type(), booking.isRoundTrip(), booking.getWait_hours(), booking.getTransport_fee(), booking.getPromotionPercentage());
        booking.setActual_total_price(priceInfo.getTotal_price());
        booking.setActualTotalPriceBeforePromotion(priceInfo.getTotal_price_before_promotion());
        booking.setActual_outward_price(priceInfo.outwardPrice);
        booking.setActual_return_price(priceInfo.returnPrice);
        booking.setActual_wait_price(priceInfo.waitPrice);
    }

    public static double calculateDriverFee(double priceWithoutTransportFee, double fee_percentage, double promotion_percentage) {
        return priceWithoutTransportFee * (fee_percentage - promotion_percentage) / 100;
    }
}
