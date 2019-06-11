package vn.ontaxi.rest.payload.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.ontaxi.common.jpa.entity.Gender;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Getter @Setter
public class CustomerDTO {

    private Long id;
    private String phone;
    private String name;
    private String email;
    private String job;
    private Date birthDay;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private Set<BehaviorDTO> behaviors;
    private List<AddressDTO> addresses = new ArrayList<>();

}
