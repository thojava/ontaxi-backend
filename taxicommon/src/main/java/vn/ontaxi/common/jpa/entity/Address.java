package vn.ontaxi.common.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class Address {

    public Address() {
    }

    public Address(Customer customer) {
        this.customer = customer;
    }

    public Address(String address, AddressType addressType, double lat, double lng, Customer customer) {
        this.address = address;
        this.addressType = addressType;
        this.lat = lat;
        this.lng = lng;
        this.customer = customer;
        this.numOfBooking = 1;
    }

    public enum AddressType {
        HOME,
        OFFICE,
        OTHER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private long id;
    private String address;
    @Enumerated(EnumType.STRING)
    private AddressType addressType;
    private double lat;
    private double lng;
    private int numOfBooking;
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Transient
    private boolean showMap;

    @Transient
    public void increaseNumOfBooking() {
        this.numOfBooking++;
    }

    public boolean isShowMap() {
        return showMap;
    }

    public void setShowMap(boolean showMap) {
        this.showMap = showMap;
    }

    public int getNumOfBooking() {
        return numOfBooking;
    }

    public void setNumOfBooking(int numOfBooking) {
        this.numOfBooking = numOfBooking;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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
    public String getCenterGeoMap() {
        return lat + ", " + lng;
    }


    public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }
}
