package vn.ontaxi.common.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.ontaxi.common.jpa.entity.Customer;
import vn.ontaxi.common.jpa.entity.CustomerBehavior;
import vn.ontaxi.common.jpa.entity.CustomerBehaviorId;

public interface CustomerBehaviorRepository extends JpaRepository<CustomerBehavior, CustomerBehaviorId>, JpaSpecificationExecutor<CustomerBehaviorId> {

    void deleteByCustomerId(long id);
}
