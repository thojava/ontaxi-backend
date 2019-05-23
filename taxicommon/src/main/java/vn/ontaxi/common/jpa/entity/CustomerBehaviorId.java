package vn.ontaxi.common.jpa.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CustomerBehaviorId implements Serializable {

    private Long customerId;
    private Long behaviorId;

    public CustomerBehaviorId() {
    }

    public CustomerBehaviorId(Long customerId, Long behaviorId) {
        this.customerId = customerId;
        this.behaviorId = behaviorId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getBehaviorId() {
        return behaviorId;
    }

    public void setBehaviorId(Long behaviorId) {
        this.behaviorId = behaviorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        CustomerBehaviorId that = (CustomerBehaviorId) o;
        return Objects.equals(customerId, that.customerId) &&
                Objects.equals(behaviorId, that.behaviorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, behaviorId);
    }

}
