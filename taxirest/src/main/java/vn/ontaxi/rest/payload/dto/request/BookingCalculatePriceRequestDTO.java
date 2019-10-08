package vn.ontaxi.rest.payload.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import vn.ontaxi.common.constant.CarTypes;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

public class BookingCalculatePriceRequestDTO {
    private String from_location;
    private String to_location;
    private Date departureTime;
    private String is_round_trip;
    private Date returnDepartureTime;
    @Enumerated(EnumType.STRING)

    private CarTypes car_type;

    public CarTypes getCar_type() {
        return car_type;
    }

    public void setCar_type(CarTypes car_type) {
        this.car_type = car_type;
    }

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

    public String getIs_round_trip() {
        return is_round_trip;
    }

    public void setIs_round_trip(String is_round_trip) {
        this.is_round_trip = is_round_trip;
    }

    public Date getReturnDepartureTime() {
        return returnDepartureTime;
    }

    public void setReturnDepartureTime(Date returnDepartureTime) {
        this.returnDepartureTime = returnDepartureTime;
    }
}
