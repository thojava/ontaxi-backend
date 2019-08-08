package vn.ontaxi.rest.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.ontaxi.common.jpa.entity.Gender;
import vn.ontaxi.rest.payload.dto.AddressDTO;
import vn.ontaxi.rest.payload.dto.BehaviorDTO;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Getter @Setter
public class CustomerUpdateInfo {

    @NotEmpty
    private Long id;
    @NotEmpty
    private String phone;
    @NotEmpty
    private String name;
    private String email;
    private String job;
    private Date birthDay;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private List<AddressDTO> addresses = new ArrayList<>();

}
