package vn.ontaxi.common.utils;

import vn.ontaxi.common.constant.CarTypes;
import vn.ontaxi.common.constant.SendToGroupOptions;
import vn.ontaxi.common.jpa.entity.Booking;
import vn.ontaxi.common.jpa.entity.Driver;
import vn.ontaxi.common.jpa.entity.PromotionPlan;
import vn.ontaxi.common.jpa.repository.PromotionPlanRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class BookingUtils {
    public final static String DELIMITER = ",";
    private final static String PREFIX_CODE = "DT";

    public static List<Driver> getDriversToBeSent(Booking booking, List<Driver> selectedDrivers, String sendToGroupOption, Iterable<Driver> allDrivers) {
        Stream<Driver> stream;
        if (SendToGroupOptions.ALL.equalsIgnoreCase(sendToGroupOption)) {
            stream = StreamSupport.stream(allDrivers.spliterator(), false);
        } else if (SendToGroupOptions.LONG_HAUL.equalsIgnoreCase(sendToGroupOption)) {
            stream = StreamSupport.stream(allDrivers.spliterator(), false);
            stream = stream.filter(Driver::isLongHaul);
        } else if (SendToGroupOptions.PRIORITY.equalsIgnoreCase(sendToGroupOption)) {
            stream = StreamSupport.stream(allDrivers.spliterator(), false);
            stream = stream.filter(driver -> driver.getLevel() == 1);
        } else {
            stream = selectedDrivers.stream();
        }

        // Filter out blocked driver
        stream = stream.filter(driver -> driver.getStatus() == Driver.Status.ACTIVATED);

        // Send to related car type
        if (booking.getCar_type() == CarTypes.N7) {
            stream = stream.filter(d -> d.getCarType() == CarTypes.N7);
        }

        return stream.collect(Collectors.toList());
    }

    public static double calculatePromotionPercentage(Date departureTime, double distance, boolean isLaterPaid, PromotionPlanRepository promotionPlanRepository) {
        if (isLaterPaid) return 0;


        List<PromotionPlan> promotionPlans = promotionPlanRepository.findAll();
        for (PromotionPlan promotionPlan : promotionPlans) {
            if (promotionPlan.getFrom_distance() <= distance && promotionPlan.getTo_distance() > distance) {
                LocalDate localDate = departureTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                int dayOfWeekValue = localDate.getDayOfWeek().getValue();
                if (dayOfWeekValue == 1) {
                    return promotionPlan.getMonday_percentage();
                } else if (dayOfWeekValue == 2) {
                    return promotionPlan.getTuesday_percentage();
                } else if (dayOfWeekValue == 3) {
                    return promotionPlan.getWednesday_percentage();
                } else if (dayOfWeekValue == 4) {
                    return promotionPlan.getThursday_percentage();
                } else if (dayOfWeekValue == 5) {
                    return promotionPlan.getFriday_percentage();
                } else if (dayOfWeekValue == 6) {
                    return promotionPlan.getSaturday_percentage();
                } else {
                    return promotionPlan.getSunday_percentage();
                }
            }
        }

        return 0;
    }

    public static String generateTicketCodeFromId(Long id) {
        if (id == null || id == 0)
            return "";
        String ticketCode = StringUtils.leftPad(id + "", 8, "0");
        return PREFIX_CODE + ticketCode;
    }

    public static Long getIdFromTicketCode(String ticketCode) {
        String code = ticketCode.replaceAll(PREFIX_CODE, "");
        return Long.parseLong(code);
    }

}
