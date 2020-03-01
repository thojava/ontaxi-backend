package vn.ontaxi.rest.payload.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import vn.ontaxi.common.constant.CarType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

@Data
public class BookingCalculatePriceResponseDTO {
    private String from_location;
    private String to_location;
    private Date departureTime;
    @Enumerated(EnumType.STRING)
    private CarType car_type;
    private double total_distance;
    private double total_price;
    private double unit_price;
    private double promotionPercentage;
    private String is_round_trip;
    private double outward_price;
    private double return_price;
    private double terrain_price;
    private double wait_price;
    private double wait_hours;
    private boolean driver_will_wait;

    @ApiModelProperty(example = "2016-01-01 15:30")
    public Date getDeparture_time() {
        return departureTime;
    }
}
