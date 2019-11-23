package vn.ontaxi.common.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ontaxi.common.jpa.entity.FeeConfiguration;
import vn.ontaxi.common.jpa.entity.NotificationConfiguration;

public interface NotificationConfigurationRepository extends JpaRepository<NotificationConfiguration, Long> {
}
