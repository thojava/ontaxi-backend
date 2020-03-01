package vn.ontaxi.rest.payload.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import vn.ontaxi.common.constant.BooleanConstants;
import vn.ontaxi.common.constant.CarType;
import vn.ontaxi.common.constant.OrderStatus;
import vn.ontaxi.common.utils.NumberUtils;
import vn.ontaxi.rest.payload.dto.request.BookingCalculatePriceRequestDTO;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

public class BookingDTO extends BookingCalculatePriceRequestDTO {

    private long id;
    private Date actual_departure_time;
    private Date arrivalTime;
    private Date outwardArrivalTime;
    private Date returnDepartureTime;
    private String booking_type;
    private double total_distance;
    private double outward_distance;
    private double return_distance;
    private boolean returnedCustomer;

    private double unit_price;
    private double totalPriceBeforePromotion;
    private double total_price;
    private double outward_price;
    private double return_price;
    private double wait_price;

    private double total_fee;
    private double actual_total_distance;
    private double actual_total_price;
    private double actualTotalPriceBeforePromotion;
    private double actual_outward_price;
    private double actual_return_price;
    private double actual_wait_price;

    private double actual_total_fee;
    private String status = OrderStatus.NEW;
    private String accepted_by;
    @Enumerated(EnumType.STRING)
    private CarType car_type;
    private String is_round_trip;
    private double wait_hours;
    private String mobile;
    private String name;
    private String email;
    private double actual_start_lon;
    private double actual_start_lat;
    private double actual_end_lon;
    private double actual_end_lat;
    private String to_drivers;
    private double transport_fee;
    private double fee_percentage;
    private String viewed = BooleanConstants.NO;
    private String isLaterPaid = BooleanConstants.NO;
    private String debtor;
    private String paidToDriver = BooleanConstants.NO;
    private double promotionPercentage;
    private String isFixedPrice = BooleanConstants.NO;
    private long surveyId;
    private String routes = "";
    private String return_routes = "";
    private short noOfGuests;

    public BookingDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBooking_type() {
        return booking_type;
    }

    public void setBooking_type(String booking_type) {
        this.booking_type = booking_type;
    }

    public double getTotal_distance() {
        return total_distance;
    }

    public void setTotal_distance(double total_distance) {
        this.total_distance = NumberUtils.roundDistance(total_distance);
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public double getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(double total_fee) {
        this.total_fee = total_fee;
    }

    public String getStatus() {
        return status;
    }

    @JsonIgnore
    public boolean isCompleted() {
        return OrderStatus.COMPLETED.equalsIgnoreCase(getStatus());
    }

    @JsonIgnore
    public boolean isOrdered() {
        return OrderStatus.ORDERED.equalsIgnoreCase(getStatus());
    }

    @JsonIgnore
    public boolean isNew() {
        return OrderStatus.NEW.equalsIgnoreCase(getStatus());
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccepted_by() {
        return accepted_by;
    }

    public void setAccepted_by(String accepted_by) {
        this.accepted_by = accepted_by;
    }

    public double getActual_total_distance() {
        return actual_total_distance;
    }

    public void setActual_total_distance(double actual_total_distance) {
        this.actual_total_distance = NumberUtils.roundDistance(actual_total_distance);
    }

    public double getActual_total_price() {
        return actual_total_price;
    }

    public void setActual_total_price(double actual_total_price) {
        this.actual_total_price = actual_total_price;
    }

    public double getActual_total_fee() {
        return actual_total_fee;
    }

    public void setActual_total_fee(double actual_total_fee) {
        this.actual_total_fee = actual_total_fee;
    }

    public CarType getCar_type() {
        return car_type;
    }

    public void setCar_type(CarType car_type) {
        this.car_type = car_type;
    }

    public String getIs_round_trip() {
        return is_round_trip;
    }

    public void setIs_round_trip(String is_round_trip) {
        this.is_round_trip = is_round_trip;
    }

    public double getWait_hours() {
        return wait_hours;
    }

    public void setWait_hours(double wait_hours) {
        this.wait_hours = wait_hours;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public double getReturn_price() {
        return return_price;
    }

    public void setReturn_price(double return_price) {
        this.return_price = return_price;
    }

    public double getWait_price() {
        return wait_price;
    }

    public void setWait_price(double wait_price) {
        this.wait_price = wait_price;
    }

    public double getOutward_price() {
        return outward_price;
    }

    public void setOutward_price(double outward_price) {
        this.outward_price = outward_price;
    }

    public double getActual_outward_price() {
        return actual_outward_price;
    }

    public void setActual_outward_price(double actual_outward_price) {
        this.actual_outward_price = actual_outward_price;
    }

    public double getActual_return_price() {
        return actual_return_price;
    }

    public void setActual_return_price(double actual_return_price) {
        this.actual_return_price = actual_return_price;
    }

    public double getActual_wait_price() {
        return actual_wait_price;
    }

    public void setActual_wait_price(double actual_wait_price) {
        this.actual_wait_price = actual_wait_price;
    }

    public double getOutward_distance() {
        return outward_distance;
    }

    public void setOutward_distance(double outward_distance) {
        this.outward_distance = outward_distance;
    }

    public double getReturn_distance() {
        return return_distance;
    }

    public void setReturn_distance(double return_distance) {
        this.return_distance = return_distance;
    }

    public Date getArrival_time() {
        return arrivalTime;
    }

    public void setArrival_time(Date arrival_time) {
        this.arrivalTime = arrival_time;
    }

    public String getTo_drivers() {
        return to_drivers;
    }

    public void setTo_drivers(String to_drivers) {
        this.to_drivers = to_drivers;
    }

    public double getActual_end_lat() {
        return actual_end_lat;
    }

    public void setActual_end_lat(double actual_end_lat) {
        this.actual_end_lat = actual_end_lat;
    }

    public double getActual_end_lon() {
        return actual_end_lon;
    }

    public void setActual_end_lon(double actual_end_lon) {
        this.actual_end_lon = actual_end_lon;
    }

    public double getActual_start_lat() {
        return actual_start_lat;
    }

    public void setActual_start_lat(double actual_start_lat) {
        this.actual_start_lat = actual_start_lat;
    }

    public double getActual_start_lon() {
        return actual_start_lon;
    }

    public void setActual_start_lon(double actual_start_lon) {
        this.actual_start_lon = actual_start_lon;
    }

    public Date getActual_departure_time() {
        return actual_departure_time;
    }

    public void setActual_departure_time(Date actual_departure_time) {
        this.actual_departure_time = actual_departure_time;
    }

    public String getRoutes() {
        return routes;
    }

    public void setRoutes(String routes) {
        this.routes = routes;
    }

    public String getReturn_routes() {
        return return_routes;
    }

    public void setReturn_routes(String return_routes) {
        this.return_routes = return_routes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTransport_fee() {
        return transport_fee;
    }

    public void setTransport_fee(double transport_fee) {
        this.transport_fee = transport_fee;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getViewed() {
        return viewed;
    }

    public void setViewed(String viewed) {
        this.viewed = viewed;
    }

    public boolean isViewed() {
        return BooleanConstants.YES.equalsIgnoreCase(viewed);
    }

    public double getFee_percentage() {
        return fee_percentage;
    }

    public void setFee_percentage(double fee_percentage) {
        this.fee_percentage = fee_percentage;
    }

    public String getIsLaterPaid() {
        return isLaterPaid;
    }

    public void setIsLaterPaid(String isLaterPaid) {
        this.isLaterPaid = isLaterPaid;
    }

    public boolean isLaterPaid() {
        return BooleanConstants.YES.equalsIgnoreCase(getIsLaterPaid());
    }

    public void setLaterPaid(boolean laterPaid) {
        isLaterPaid = laterPaid ? BooleanConstants.YES : BooleanConstants.NO;
    }

    public String getDebtor() {
        return debtor;
    }

    public void setDebtor(String debtor) {
        this.debtor = debtor;
    }

    public String getPaidToDriver() {
        return paidToDriver;
    }

    public boolean isPaidToDriver() {
        return BooleanConstants.YES.equalsIgnoreCase(paidToDriver);
    }

    public void setPaidToDriver(String paidToDriver) {
        this.paidToDriver = paidToDriver;
    }

    public double getPromotionPercentage() {
        return promotionPercentage;
    }

    public void setPromotionPercentage(double promotionPercentage) {
        this.promotionPercentage = promotionPercentage;
    }

    public double getTotalPriceBeforePromotion() {
        return totalPriceBeforePromotion;
    }

    public void setTotalPriceBeforePromotion(double totalPriceBeforePromotion) {
        this.totalPriceBeforePromotion = totalPriceBeforePromotion;
    }

    public double getActualTotalPriceBeforePromotion() {
        return actualTotalPriceBeforePromotion;
    }

    public void setActualTotalPriceBeforePromotion(double actualTotalPriceBeforePromotion) {
        this.actualTotalPriceBeforePromotion = actualTotalPriceBeforePromotion;
    }

    public Date getOutwardArrivalTime() {
        return outwardArrivalTime;
    }

    public void setOutwardArrivalTime(Date outwardArrivalTime) {
        this.outwardArrivalTime = outwardArrivalTime;
    }

    public Date getReturnDepartureTime() {
        return returnDepartureTime;
    }

    public void setReturnDepartureTime(Date returnDepartureTime) {
        this.returnDepartureTime = returnDepartureTime;
    }

    public String getIsFixedPrice() {
        return isFixedPrice;
    }

    public void setIsFixedPrice(String isFixedPrice) {
        this.isFixedPrice = isFixedPrice;
    }

    public void setFixedPrice(boolean fixedPrice) {
        this.isFixedPrice = fixedPrice ? BooleanConstants.YES : BooleanConstants.NO;
    }

    public boolean isFixedPrice() {
        return BooleanConstants.YES.equalsIgnoreCase(isFixedPrice);
    }

    public long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(long surveyId) {
        this.surveyId = surveyId;
    }

    public boolean isReturnedCustomer() {
        return returnedCustomer;
    }

    public void setReturnedCustomer(boolean returnedCustomer) {
        this.returnedCustomer = returnedCustomer;
    }

    public void setUnit_price(double unit_price) {
        this.unit_price = unit_price;
    }

    public short getNoOfGuests() {
        return noOfGuests;
    }

    public void setNoOfGuests(short noOfGuests) {
        this.noOfGuests = noOfGuests;
    }
}
