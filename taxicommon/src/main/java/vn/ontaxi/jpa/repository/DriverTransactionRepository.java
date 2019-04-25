package vn.ontaxi.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ontaxi.jpa.entity.DriverTransaction;

import java.util.List;

public interface DriverTransactionRepository extends JpaRepository<DriverTransaction, String> {
    List<DriverTransaction> findByDriverOrderByLastUpdatedDatetimeDesc(String driver);
}