package vn.ontaxi.utils;

import org.apache.commons.lang3.StringUtils;
import vn.ontaxi.jpa.entity.Booking;
import vn.ontaxi.jpa.entity.Driver;
import vn.ontaxi.service.PriceCalculator;

public class SMSContentBuilder {
    private static final String HOTLINE = "0869342913";
    private static final String DRIVER_ACCEPTED_SMS_TEMPLATE = "HOME TAXI: Quy khach da dat xe thanh cong. Lai xe %s Bien So %s se don khach vao luc %s. Cam On Quy Khach";
    private static final String SINGLE_ROUTE_TEMPLATE = "HOME TAXI: Khach Hang: %s . %s -> %s : %s km * %d d/km = %s. Phi Cau Duong: %s. Tong Tien %s, duoc giam %d %%, Thanh Toan %s. Hotline: %s. Cam On Quy Khach";
    private static final String RETURN_ROUTE_TEMPLATE = "HOME TAXI: Khach Hang: %s . %s -> %s : %skm * %dd/km = %s. %s -> %s : %skm * %dd/km * %s = %s" +
            ". Phi Cau Duong: %s. Giờ Chờ %s * %dd/h = %s ." +
            " Tong Tien %s, duoc giam %d %%, Thanh Toan %s. Hotline: %s. Cam On Quy Khach";

    public static String buildDriverAcceptedSMSContent(Booking booking, Driver acceptedDriver) {
        return String.format(DRIVER_ACCEPTED_SMS_TEMPLATE,
                acceptedDriver.getMobile(),
                acceptedDriver.getLicense_plates(),
                DateUtils.hhMMddM.format(booking.getDeparture_time()));
    }


    public static String buildCompleteOrderSMSContent(Booking booking) {
        String fromLocation = org.apache.commons.lang3.StringUtils.defaultIfBlank(booking.getFrom_city(), booking.getFrom_location());
        String toLocation = StringUtils.defaultIfBlank(booking.getTo_city(), booking.getTo_location());
        int pricePerKm = (int) booking.getUnit_price();
        if (!booking.isRoundTrip()) {
            return String.format(SINGLE_ROUTE_TEMPLATE,
                    booking.getMobile(),
                    fromLocation,
                    toLocation,
                    NumberUtils.roundDistance(booking.getActual_total_distance()),
                    pricePerKm,
                    NumberUtils.formatAmountInVND(booking.getActualTotalPriceBeforePromotion() - booking.getTransport_fee()),
                    NumberUtils.formatAmountInVND(booking.getTransport_fee()),
                    NumberUtils.formatAmountInVND(booking.getActualTotalPriceBeforePromotion()),
                    (int) booking.getPromotionPercentage(),
                    NumberUtils.formatAmountInVND(booking.getActual_total_price()),
                    HOTLINE);
        } else {
            double lowDistance, highDistance, lowPrice, highPrice;
            String lowFromLocation, highFromLocation, lowToLocation, highToLocation;
            if (booking.getOutward_distance() > booking.getReturn_distance()) {
                lowDistance = booking.getReturn_distance();
                lowPrice = booking.getActual_return_price();
                highDistance = booking.getOutward_distance();
                highPrice = booking.getActual_outward_price();
                lowFromLocation = toLocation;
                lowToLocation = fromLocation;
                highFromLocation = fromLocation;
                highToLocation = toLocation;
            } else {
                lowDistance = booking.getOutward_distance();
                lowPrice = booking.getActual_return_price();
                highDistance = booking.getReturn_distance();
                highPrice = booking.getActual_outward_price();

                lowFromLocation = fromLocation;
                lowToLocation = toLocation;
                highFromLocation = toLocation;
                highToLocation = fromLocation;
            }
            double feeHours = Math.max(0, booking.getWait_hours() - PriceUtils.getFreeWaitTime(highDistance));
            int pricePerWaitHour = PriceUtils.getPricePerWaitHour(booking.getCar_type());

            return String.format(RETURN_ROUTE_TEMPLATE,
                    booking.getMobile(),
                    highFromLocation, highToLocation, NumberUtils.roundDistance(highDistance), pricePerKm, NumberUtils.formatAmountInVND(highPrice),
                    lowFromLocation, lowToLocation, NumberUtils.roundDistance(lowDistance), pricePerKm, PriceUtils.getReturnRoundPercentage(highDistance) + "%",
                    NumberUtils.formatAmountInVND(lowPrice),
                    NumberUtils.formatAmountInVND(booking.getTransport_fee()),
                    feeHours + "h",
                    pricePerWaitHour,
                    NumberUtils.formatAmountInVND(booking.getActual_wait_price()),
                    NumberUtils.formatAmountInVND(booking.getActual_total_price()),
                    (int)booking.getPromotionPercentage(),
                    NumberUtils.formatAmountInVND(booking.getActual_total_price()),
                    HOTLINE);
        }
    }
}
