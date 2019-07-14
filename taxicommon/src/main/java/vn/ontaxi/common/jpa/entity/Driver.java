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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private long id;
    private String email;
    private String mobile;
    private String name;
    private Date birthDay;
    private String license_plates;
    private LicenseTypes licenseType;
    private double amount;
    @Enumerated(EnumType.STRING)
    private CarTypes carType;
    private int level;
    private boolean blocked;
    private boolean deleted;
    private boolean airport;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
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
