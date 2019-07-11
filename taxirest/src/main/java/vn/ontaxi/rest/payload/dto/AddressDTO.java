package vn.ontaxi.rest.payload.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import springfox.documentation.annotations.ApiIgnore;
import vn.ontaxi.common.jpa.entity.Address;
import vn.ontaxi.common.jpa.entity.Customer;

import javax.persistence.*;

@Entity
public class AddressDTO {

    public AddressDTO() {
    }

    @JsonIgnore
    private long id;
    private String address;
    @Enumerated(EnumType.STRING)
    private Address.AddressType addressType = Address.AddressType.HOME;
    private double lat;
    private double lng;
    @JsonIgnore
    private int numOfBooking;

    @Transient
    public void increaseNumOfBooking() {
        this.numOfBooking++;
    }

    public int getNumOfBooking() {
        return numOfBooking;
    }

    public void setNumOfBooking(int numOfBooking) {
        this.numOfBooking = numOfBooking;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Transient
    @JsonIgnore
    public String getCenterGeoMap() {
        return lat + ", " + lng;
    }


    public Address.AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(Address.AddressType addressType) {
        this.addressType = addressType;
    }
}
