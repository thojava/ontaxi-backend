package vn.ontaxi.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ontaxi.jpa.entity.Driver;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    Driver findByEmail(String email);

    List<Driver> findAllByOrderByCarType();

    List<Driver> findByAirportFalseAndDeletedFalseOrderByCarType();

    List<Driver> findByAirportTrueAndDeletedFalseOrderByCarType();
}
