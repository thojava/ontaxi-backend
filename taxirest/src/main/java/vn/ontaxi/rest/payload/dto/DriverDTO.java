package vn.ontaxi.rest.payload.dto;

import io.swagger.annotations.ApiModelProperty;
import vn.ontaxi.common.constant.CarTypes;
import vn.ontaxi.common.constant.LicenseTypes;
import vn.ontaxi.common.jpa.entity.Driver;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class DriverDTO {

    @ApiModelProperty(hidden = true)
    private Long id;
    @NotBlank(message = "Email không được để trống")
    private String email;
    @NotBlank(message = "SĐT không được để trống")
    private String mobile;
    @NotBlank(message = "Tên không được để trống")
    private String name;
    @NotNull(message = "Ngày sinh không được để trống")
    private Date birthDay;
    @NotBlank(message = "CMT không được để trống")
    private String identity;
    @NotBlank(message = "Số tài khoản không được để trống")
    private String bankId;
    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;
    @NotBlank(message = "Địa chỉ thường trú không được để trống")
    private String currentAddress;
    @NotNull(message = "Loại bằng lái không được để trống")
    private LicenseTypes licenseType;
    @NotBlank(message = "Số bằng lái không được để trống")
    private String licenseNumber;
    private boolean airport;
    @NotNull(message = "Loại xe không được để trống")
    @Enumerated(EnumType.STRING)
    private CarTypes carType;
    @NotBlank(message = "Hãng xe không được để trống")
    private String carBrand;
    @NotBlank(message = "Năm sản xuất không được để trống")
    private String manufactureYear;
    @NotBlank(message = "Biển số xe không được để trống")
    private String license_plates;
    @ApiModelProperty(hidden = true)
    private int level;
    @ApiModelProperty(hidden = true)
    private boolean deleted;
    @ApiModelProperty(hidden = true)
    private double amount;
    @ApiModelProperty(hidden = true)
    @Enumerated(EnumType.STRING)
    private Driver.Status status;

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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
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

    public LicenseTypes getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(LicenseTypes licenseType) {
        this.licenseType = licenseType;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public boolean isAirport() {
        return airport;
    }

    public void setAirport(boolean airport) {
        this.airport = airport;
    }

    public CarTypes getCarType() {
        return carType;
    }

    public void setCarType(CarTypes carType) {
        this.carType = carType;
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

    public String getLicense_plates() {
        return license_plates;
    }

    public void setLicense_plates(String license_plates) {
        this.license_plates = license_plates;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Driver.Status getStatus() {
        return status;
    }

    public void setStatus(Driver.Status status) {
        this.status = status;
    }
}
