package vn.ontaxi.common.jpa.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class PriceConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private double normal_4;
    private double good_4;
    private double normal_7;
    private double normal_16;
    private double return_round_percentage;
    private double return_round_percentage_without_waiting;
}
