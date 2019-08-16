package vn.ontaxi.rest.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.ontaxi.common.jpa.entity.Gender;
import vn.ontaxi.rest.payload.dto.AddressDTO;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Getter @Setter
public class CustomerUpdateInfo {

    @NotBlank
    private Long id;
    @NotBlank
    private String phone;
    @NotBlank
    private String name;
    private String email;
    private String job;
    private Date birthDay;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private List<AddressDTO> addresses = new ArrayList<>();

}
