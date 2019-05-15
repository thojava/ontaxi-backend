package vn.ontaxi.common.jpa.repository;

import org.springframework.data.repository.CrudRepository;
import vn.ontaxi.common.jpa.entity.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
}
