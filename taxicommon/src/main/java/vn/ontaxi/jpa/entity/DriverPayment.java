package vn.ontaxi.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class DriverPayment extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private long id;
    @ManyToOne
    @JoinColumn(name = "driver", referencedColumnName = "email", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Driver driver;
    private double amount;
    private String paymentType;
    private String reason;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    @JsonIgnore
    public String getKey() {
        return String.valueOf(getId());
    }
}
