package vn.ontaxi.common.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ontaxi.common.jpa.entity.FeeConfiguration;
import vn.ontaxi.common.jpa.entity.PriceConfiguration;

public interface FeeConfigurationRepository extends JpaRepository<FeeConfiguration, Long> {
}
