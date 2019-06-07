package vn.ontaxi.common.jpa.entity;

import javax.persistence.*;

@Entity
public class EmailSchedulerVsCustomer {

    public EmailSchedulerVsCustomer() {
    }

    public EmailSchedulerVsCustomer(Long customerId, Long emailSchedulerId) {
        this.customerId = customerId;
        this.emailSchedulerId = emailSchedulerId;
        this.count = 1;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private Long emailSchedulerId;
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getEmailSchedulerId() {
        return emailSchedulerId;
    }

    public void setEmailSchedulerId(Long emailSchedulerId) {
        this.emailSchedulerId = emailSchedulerId;
    }
}
