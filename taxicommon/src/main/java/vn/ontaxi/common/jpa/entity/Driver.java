package vn.ontaxi.common.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import vn.ontaxi.common.constant.CarTypes;
import vn.ontaxi.common.constant.LicenseTypes;
import vn.ontaxi.common.utils.RoundUtils;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Driver extends AbstractEntity {

    public enum Status {
        ACTIVATED("Đã kích hoạt"), REGISTRY("Chưa kích hoạt"), BLOCKED("Đã khóa");
        private String name;

        Status(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    private String email;
    private String mobile;
    private String name;
    private Date birthDay;
    private String identity;
    private String bankId;
    private String address;
    private String currentAddress;
    private LicenseTypes licenseType;
    private String licenseNumber;
    private boolean airport;
    @Enumerated(EnumType.STRING)
    private CarTypes carType;
    private String carBrand;
    private String manufactureYear;
    private String license_plates;
    private int level;
    private boolean deleted;
    private double amount;
    @Enumerated(EnumType.STRING)
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = RoundUtils.round(amount, 2);
    }

    public void decreaseAmt(double amt, Logger logger) {
        String s = "Driver " + this.getEmail() + " decrease amount " + amt + " from " + this.amount;
        setAmount(this.amount - amt);
        s += " to " + this.amount;
        logger.debug(s);
    }

    public void increaseAmt(double amt, Logger logger) {
        String s = "Driver " + this.getEmail() + " increase amount " + amt + " from " + this.amount;
        setAmount(this.amount + amt);
        s += " to " + this.amount;
        logger.debug(s);
    }

    @JsonIgnore
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @JsonIgnore
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public String getLicense_plates() {
        return license_plates;
    }

    public void setLicense_plates(String license_plates) {
        this.license_plates = license_plates;
    }

    @JsonIgnore
    public CarTypes getCarType() {
        return carType;
    }

    public void setCarType(CarTypes carType) {
        this.carType = carType;
    }

    @JsonIgnore
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean equals(Object obj) {
        return obj instanceof Driver && this.getId() == ((Driver) obj).getId();
    }

    @JsonIgnore
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @JsonIgnore
    public boolean isAirport() {
        return airport;
    }

    @JsonIgnore
    public boolean isLongHaul() {
        return !isAirport();
    }

    public void setAirport(boolean airport) {
        this.airport = airport;
    }

    @JsonIgnore
    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    @JsonIgnore
    public LicenseTypes getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(LicenseTypes licenseType) {
        this.licenseType = licenseType;
    }

    @Override
    @JsonIgnore
    public String getKey() {
        return String.valueOf(getId());
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getManufactureYear() {
        return manufactureYear;
    }

    public void setManufactureYear(String manufactureYear) {
        this.manufactureYear = manufactureYear;
    }

    @Override
    public String toString() {
        return "{" +
                "" + id +
                ",'" + email + '\'' +
                ",'" + mobile + '\'' +
                ",'" + name + '\'' +
                ",'" + license_plates + '\'' +
                "," + amount +
                ",'" + carType + '\'' +
                '}';
    }
}
