package vn.ontaxi.rest.payload.dto.request;

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
    private boolean driver_will_wait;
    private Date returnDepartureTime;
    @Enumerated(EnumType.STRING)

    private CarTypes car_type;

    public CarTypes getCar_type() {
        return car_type;
    }

    public void setCar_type(CarTypes car_type) {
        this.car_type = car_type;
    }

    @ApiModelProperty(example = "Hà Nội", value="From location of the trip")
    public String getFrom_location() {
        return from_location;
    }

    public void setFrom_location(String from_location) {
        this.from_location = from_location;
    }

    @ApiModelProperty(example = "Bắc Ninh", value="To location of the trip")
    public String getTo_location() {
        return to_location;
    }

    public void setTo_location(String to_location) {
        this.to_location = to_location;
    }

    @ApiModelProperty(example = "2016-01-01 15:30", value="Departure time of the trip")
    public Date getDeparture_time() {
        return departureTime;
    }

    public void setDeparture_time(Date departure_time) {
        this.departureTime = departure_time;
    }

    @ApiModelProperty(example = "N", value="Values: N or Y to indicate if this booking is one way trip or round trip")
    public String getIs_round_trip() {
        return is_round_trip;
    }

    public void setIs_round_trip(String is_round_trip) {
        this.is_round_trip = is_round_trip;
    }

    @ApiModelProperty(example = "2016-01-01 21:30", value="In case the booking is round trip, need to choose return departure time")
    public Date getReturnDepartureTime() {
        return returnDepartureTime;
    }

    public void setReturnDepartureTime(Date returnDepartureTime) {
        this.returnDepartureTime = returnDepartureTime;
    }

    @ApiModelProperty(example = "true", value="Driver will wait customer to return or not")
    public boolean isDriver_will_wait() {
        return driver_will_wait;
    }

    public void setDriver_will_wait(boolean driver_will_wait) {
        this.driver_will_wait = driver_will_wait;
    }
}
