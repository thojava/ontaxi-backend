package vn.ontaxi.common.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ontaxi.common.jpa.entity.EmailSchedulerVsCustomer;

public interface EmailSchedulerVsCustomerRepository extends JpaRepository<EmailSchedulerVsCustomer, Long> {

    EmailSchedulerVsCustomer findByCustomerIdAndAndEmailSchedulerId(Long customerId, Long emailSchedulerId);

}
