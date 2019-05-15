package vn.ontaxi.common.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ontaxi.common.jpa.entity.DriverPayment;

import java.util.List;

public interface DriverPaymentRepository extends JpaRepository<DriverPayment, Long> {
    public List<DriverPayment> findAllByOrderByIdDesc();
}
