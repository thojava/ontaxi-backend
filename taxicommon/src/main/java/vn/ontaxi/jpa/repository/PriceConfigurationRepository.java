package vn.ontaxi.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ontaxi.jpa.entity.PriceConfiguration;

public interface PriceConfigurationRepository extends JpaRepository<PriceConfiguration, Long> {
}
