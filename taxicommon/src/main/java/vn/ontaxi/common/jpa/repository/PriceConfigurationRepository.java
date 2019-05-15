package vn.ontaxi.common.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ontaxi.common.jpa.entity.PriceConfiguration;

public interface PriceConfigurationRepository extends JpaRepository<PriceConfiguration, Long> {
}
