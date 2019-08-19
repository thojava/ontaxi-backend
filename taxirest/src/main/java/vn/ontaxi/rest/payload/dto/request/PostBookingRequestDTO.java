package vn.ontaxi.rest.payload.dto.request;

import javax.validation.constraints.NotBlank;

public class PostBookingRequestDTO extends BookingCalculatePriceRequestDTO {
    @NotBlank
    private String mobile;
    @NotBlank
    private String name;
    private String email;
    private String is_round_trip;
    private String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIs_round_trip() {
        return is_round_trip;
    }

    public void setIs_round_trip(String is_round_trip) {
        this.is_round_trip = is_round_trip;
    }
}
