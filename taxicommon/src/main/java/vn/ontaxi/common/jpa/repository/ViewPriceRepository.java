package vn.ontaxi.common.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ontaxi.common.jpa.entity.ViewPrice;

import java.util.Date;
import java.util.List;

public interface ViewPriceRepository extends JpaRepository<ViewPrice, Long> {
    List<ViewPrice> findByCreatedDatetimeBetweenOrderByCreatedDatetimeDesc(Date startTime, Date endTime);
}
