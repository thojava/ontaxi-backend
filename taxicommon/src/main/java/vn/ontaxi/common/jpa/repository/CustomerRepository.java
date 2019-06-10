package vn.ontaxi.common.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.ontaxi.common.jpa.entity.Customer;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    List<Customer> findByPhone(String phone);

    Customer findByPhoneOrEmail(String phone, String email);
}
