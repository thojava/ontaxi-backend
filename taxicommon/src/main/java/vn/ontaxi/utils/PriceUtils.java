package vn.ontaxi.utils;

import vn.ontaxi.constant.CarTypes;
import vn.ontaxi.model.PriceInfo;

public class PriceUtils {
    public static PriceInfo calculatePrice(double pricePerKm, double outwardDistant, double returnDistant, String car_type, boolean isRoundTrip,
                                           double wait_hours, double transportFee, double promotionPercentage) {
        if (isRoundTrip) {
            double lowDistance = Math.min(outwardDistant, returnDistant);
            double highDistance = Math.max(outwardDistant, returnDistant);

            double freeWaitTime = getFreeWaitTime(highDistance);

            double outwardPrice = highDistance * pricePerKm;

            double returnPrice = lowDistance * pricePerKm * getReturnRoundPercentage(lowDistance) / 100;

            double waitPrice = Math.max(0, wait_hours - freeWaitTime) * getPricePerWaitHour(car_type);

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
        if (distance <= 20) {
            return 0;
        } else if (distance <= 30) {
            return 50;
        } else if (distance <= 100) {
            return 30;
        } else {
            return 20;
        }
    }

    public static int getPricePerWaitHour(String car_type) {
        if (CarTypes.NORMAL_4.equalsIgnoreCase(car_type)) {
            return 40000;
        } else if (CarTypes.GOOD_4.equalsIgnoreCase(car_type)) {
            return 40000;
        } else if (CarTypes.NORMAL_7.equalsIgnoreCase(car_type)) {
            return 50000;
        }

        throw new IllegalArgumentException("Invalid type " + car_type);
    }
}
