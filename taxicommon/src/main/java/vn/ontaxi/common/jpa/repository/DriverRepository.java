package vn.ontaxi.common.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ontaxi.common.jpa.entity.Driver;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    Driver findByEmail(String email);

    Driver findByMobile(String phone);

    Driver findByEmailAndBlockedFalse(String email);

    List<Driver> findAllByOrderByCarType();

    List<Driver> findByAirportFalseAndDeletedFalseOrderByCarType();

    List<Driver> findByAirportTrueAndDeletedFalseOrderByCarType();
}
