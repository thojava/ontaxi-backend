package vn.ontaxi.rest.payload;

import javax.validation.constraints.NotEmpty;

public class CustomerLogin {

    @NotEmpty
    private String emailOrPhone;
    @NotEmpty
    private String password;

    public String getEmailOrPhone() {
        return emailOrPhone;
    }

    public void setEmailOrPhone(String emailOrPhone) {
        this.emailOrPhone = emailOrPhone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
