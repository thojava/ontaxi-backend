package vn.ontaxi.common.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.ontaxi.common.jpa.entity.Behavior;

public interface BehaviorRepository extends JpaRepository<Behavior, Long>, JpaSpecificationExecutor<Behavior> {

}
