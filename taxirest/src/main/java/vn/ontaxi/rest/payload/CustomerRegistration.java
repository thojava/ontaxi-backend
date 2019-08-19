package vn.ontaxi.rest.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.ontaxi.common.jpa.entity.Gender;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@NoArgsConstructor
@Getter @Setter
public class CustomerRegistration {

    @NotBlank(message = "SĐT không được để trống")
    private String phone;
    @NotBlank(message = "Tên không được để trống")
    private String name;
    @NotBlank(message = "Email không được để trống")
    private String email;
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
    private String job;
    private Date birthDay;
    @Enumerated(EnumType.STRING)
    private Gender gender;

}
