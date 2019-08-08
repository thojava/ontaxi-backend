package vn.ontaxi.rest.payload.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
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
}
