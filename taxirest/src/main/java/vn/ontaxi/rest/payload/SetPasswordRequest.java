package vn.ontaxi.rest.payload;

import javax.validation.constraints.NotBlank;

public class SetPasswordRequest {

    @NotBlank
    private String password;
    @NotBlank
    private String token;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
