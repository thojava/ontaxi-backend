package vn.ontaxi.common.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ontaxi.common.jpa.entity.CustomerGroup;

public interface CustomerGroupRepository extends JpaRepository<CustomerGroup, Long> {
}
