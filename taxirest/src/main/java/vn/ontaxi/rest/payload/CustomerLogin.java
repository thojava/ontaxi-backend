package vn.ontaxi.rest.payload;

import javax.validation.constraints.NotEmpty;

public class CustomerLogin {

    @NotEmpty
    private String phone;
    @NotEmpty
    private String password;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
