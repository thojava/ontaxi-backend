package vn.ontaxi.rest.payload.dto;

import com.google.maps.model.LatLng;
import vn.ontaxi.common.jpa.entity.Driver;

public class DriverInfoDTO {

    private String name;
    private String phone;
    private String licensePlates;
    private LatLng location;

    public DriverInfoDTO() {
    }

    public DriverInfoDTO(Driver driver, LatLng location) {
        this.name = driver.getName();
        this.phone = driver.getMobile();
        this.licensePlates = driver.getLicense_plates();
        this.location = location;
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
