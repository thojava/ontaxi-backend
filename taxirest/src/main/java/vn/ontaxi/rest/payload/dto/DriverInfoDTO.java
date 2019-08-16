package vn.ontaxi.rest.payload.dto;

import com.google.maps.model.LatLng;
import io.swagger.annotations.ApiModelProperty;
import vn.ontaxi.common.constant.CarTypes;
import vn.ontaxi.common.jpa.entity.Driver;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class DriverInfoDTO {

    @NotBlank(message = "Tên người dùng không được để trống")
    private String name;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    @NotBlank
    private String licensePlates;
    @ApiModelProperty(hidden = true)
    private LatLng location;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;
    private CarTypes carTypes;
    private boolean airport;

    public DriverInfoDTO() {
    }

    public DriverInfoDTO(Driver driver) {
        this.name = driver.getName();
        this.phone = driver.getMobile();
        this.licensePlates = driver.getLicense_plates();
    }

    public DriverInfoDTO(Driver driver, LatLng location) {
        this.name = driver.getName();
        this.phone = driver.getMobile();
        this.licensePlates = driver.getLicense_plates();
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CarTypes getCarTypes() {
        return carTypes;
    }

    public void setCarTypes(CarTypes carTypes) {
        this.carTypes = carTypes;
    }

    public boolean isAirport() {
        return airport;
    }

    public void setAirport(boolean airport) {
        this.airport = airport;
    }

    public String getLicensePlates() {
        return licensePlates;
    }

    public void setLicensePlates(String licensePlates) {
        this.licensePlates = licensePlates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
