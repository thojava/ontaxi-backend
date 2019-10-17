package vn.ontaxi.rest.payload.dto.request;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

public class PostBookingRequestDTO extends BookingCalculatePriceRequestDTO {
    @NotBlank
    private String mobile;
    @NotBlank
    private String name;
    private String email;
    private String note;

    @ApiModelProperty(value="Some additional notes about the trip which customer want to send to driver")
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @ApiModelProperty(value="Customer phone number", required = true)
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @ApiModelProperty(value="Customer name", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(value="Customer email address")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
