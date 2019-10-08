package vn.ontaxi.rest.payload.dto.response;

import io.swagger.annotations.ApiModelProperty;
import vn.ontaxi.common.constant.CarTypes;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

public class BookingCalculatePriceResponseDTO {
    private String from_location;
    private String to_location;
    private Date departureTime;
    @Enumerated(EnumType.STRING)
    private CarTypes car_type;
    private double total_distance;
    private double total_price;
    private double unit_price;
    private double promotionPercentage;
    private String is_round_trip;
    private double outward_price;
    private double return_price;
    private double wait_price;
    private double wait_hours;

    public String getFrom_location() {
        return from_location;
    }

    public void setFrom_location(String from_location) {
        this.from_location = from_location;
    }

    public String getTo_location() {
        return to_location;
    }

    public void setTo_location(String to_location) {
        this.to_location = to_location;
    }

    @ApiModelProperty(example = "2016-01-01 15:30")
    public Date getDeparture_time() {
        return departureTime;
    }

    public void setDeparture_time(Date departure_time) {
        this.departureTime = departure_time;
    }

    public CarTypes getCar_type() {
        return car_type;
    }

    public void setCar_type(CarTypes car_type) {
        this.car_type = car_type;
    }

    public double getTotal_distance() {
        return total_distance;
    }

    public void setTotal_distance(double total_distance) {
        this.total_distance = total_distance;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public double getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(double unit_price) {
        this.unit_price = unit_price;
    }

    public double getPromotionPercentage() {
        return promotionPercentage;
    }

    public void setPromotionPercentage(double promotionPercentage) {
        this.promotionPercentage = promotionPercentage;
    }

    public String getIs_round_trip() {
        return is_round_trip;
    }

    public void setIs_round_trip(String is_round_trip) {
        this.is_round_trip = is_round_trip;
    }

    public double getOutward_price() {
        return outward_price;
    }

    public void setOutward_price(double outward_price) {
        this.outward_price = outward_price;
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

    public double getWait_hours() {
        return wait_hours;
    }

    public void setWait_hours(double wait_hours) {
        this.wait_hours = wait_hours;
    }
}
