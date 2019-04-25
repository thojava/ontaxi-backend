package vn.ontaxi.jpa.repository;

import org.springframework.data.repository.CrudRepository;
import vn.ontaxi.jpa.entity.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
}
