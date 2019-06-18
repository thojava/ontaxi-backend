package vn.ontaxi.common.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ontaxi.common.jpa.entity.Customer;
import vn.ontaxi.common.jpa.entity.CustomerAccount;

public interface CustomerAccountRepository extends JpaRepository<CustomerAccount, Long> {

    CustomerAccount findByToken(String token);

    CustomerAccount findByCustomerEmailOrCustomerPhone(String email, String phone);

    CustomerAccount findByCustomerEmail(String email);

    CustomerAccount findByPhoneOrEmail(String phone, String email);
}
