package vn.ontaxi.common.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class DriverTransaction extends AbstractEntity {
    @Id
    private String id;
    private Long driver;
    private double amount;
    private String paymentType;
    private String reason;

    @Override
    @JsonIgnore
    public String getKey() {
        return getId();
    }
}
