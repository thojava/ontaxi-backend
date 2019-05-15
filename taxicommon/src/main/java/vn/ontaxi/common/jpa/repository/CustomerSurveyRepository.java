package vn.ontaxi.common.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.ontaxi.common.jpa.entity.CustomerSurvey;

public interface CustomerSurveyRepository extends JpaRepository<CustomerSurvey, Long>, JpaSpecificationExecutor<CustomerSurvey> {
}
