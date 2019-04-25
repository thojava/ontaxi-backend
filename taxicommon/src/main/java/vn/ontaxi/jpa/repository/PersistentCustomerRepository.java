package vn.ontaxi.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ontaxi.jpa.entity.PersistentCustomer;

public interface PersistentCustomerRepository extends JpaRepository<PersistentCustomer, String> {
}
