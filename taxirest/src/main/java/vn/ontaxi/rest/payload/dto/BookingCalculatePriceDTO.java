package vn.ontaxi.rest.payload.dto;

import vn.ontaxi.common.constant.CarTypes;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

public class BookingCalculatePriceDTO {
    private String from_location;
    private String from_city;
    private String from_district;
    private String to_location;
    private String to_city;
    private String to_district;
    private Date departureTime;
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

    public String getFrom_city() {
        return from_city;
    }

    public void setFrom_city(String from_city) {
        this.from_city = from_city;
    }

    public String getFrom_district() {
        return from_district;
    }

    public void setFrom_district(String from_district) {
        this.from_district = from_district;
    }

    public String getTo_location() {
        return to_location;
    }

    public void setTo_location(String to_location) {
        this.to_location = to_location;
    }

    public String getTo_city() {
        return to_city;
    }

    public void setTo_city(String to_city) {
        this.to_city = to_city;
    }

    public String getTo_district() {
        return to_district;
    }

    public void setTo_district(String to_district) {
        this.to_district = to_district;
    }

    public Date getDeparture_time() {
        return departureTime;
    }

    public void setDeparture_time(Date departure_time) {
        this.departureTime = departure_time;
    }
}
