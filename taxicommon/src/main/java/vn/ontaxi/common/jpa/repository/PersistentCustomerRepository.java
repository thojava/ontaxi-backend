package vn.ontaxi.common.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ontaxi.common.jpa.entity.PersistentCustomer;

public interface PersistentCustomerRepository extends JpaRepository<PersistentCustomer, String> {
}
