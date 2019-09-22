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
    @Enumerated(EnumType.STRING)

    private CarTypes car_type;
    @ApiModelProperty(notes = "When invoked from the first booking page this value should be set as true." +
            "Later when visitor change the booking option to see the updated price this value should be set as false")
    private boolean firstPriceView;

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

    public boolean isFirstPriceView() {
        return firstPriceView;
    }

    public void setFirstPriceView(boolean firstPriceView) {
        this.firstPriceView = firstPriceView;
    }
}
