package vn.ontaxi.common.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ontaxi.common.jpa.entity.EmailScheduler;

public interface EmailScheduleRepository extends JpaRepository<EmailScheduler, Long> {
}
