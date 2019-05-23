package vn.ontaxi.common.jpa.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class CustomerBehavior implements Serializable {

    @EmbeddedId
    private CustomerBehaviorId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("customerId")
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("behaviorId")
    private Behavior behavior;

    public CustomerBehavior() {
    }

    public CustomerBehavior(Customer customer, Behavior behavior) {
        this.customer = customer;
        this.behavior = behavior;
        this.id = new CustomerBehaviorId(this.customer.getId(), this.behavior.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        CustomerBehavior that = (CustomerBehavior) o;
        return Objects.equals(customer, that.customer) &&
                Objects.equals(behavior, that.behavior);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customer, behavior);
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Behavior getBehavior() {
        return behavior;
    }

    public void setBehavior(Behavior behavior) {
        this.behavior = behavior;
    }
}
