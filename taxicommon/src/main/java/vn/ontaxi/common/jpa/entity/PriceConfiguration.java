package vn.ontaxi.common.jpa.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PriceConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private double normal_4;
    private double good_4;
    private double normal_7;

    public double getNormal_4() {
        return normal_4;
    }

    public void setNormal_4(double normal_4) {
        this.normal_4 = normal_4;
    }

    public double getGood_4() {
        return good_4;
    }

    public void setGood_4(double good_4) {
        this.good_4 = good_4;
    }

    public double getNormal_7() {
        return normal_7;
    }

    public void setNormal_7(double normal_7) {
        this.normal_7 = normal_7;
    }
}
