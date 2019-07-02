package vn.ontaxi.common.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.ontaxi.common.jpa.entity.EmailTemplateHeaderFooter;

import java.util.List;

@Repository
public interface EmailTemplateHeaderFooterRepository extends JpaRepository<EmailTemplateHeaderFooter, Long> {

    List<EmailTemplateHeaderFooter> findByActiveTrue();

}
