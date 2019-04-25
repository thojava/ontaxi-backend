package vn.ontaxi.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.ontaxi.jpa.entity.CustomerSurvey;

public interface CustomerSurveyRepository extends JpaRepository<CustomerSurvey, Long>, JpaSpecificationExecutor<CustomerSurvey> {
}
