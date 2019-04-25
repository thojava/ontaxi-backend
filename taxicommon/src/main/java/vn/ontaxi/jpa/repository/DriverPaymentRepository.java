package vn.ontaxi.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ontaxi.jpa.entity.DriverPayment;

import java.util.List;

public interface DriverPaymentRepository extends JpaRepository<DriverPayment, Long> {
    public List<DriverPayment> findAllByOrderByIdDesc();
}
