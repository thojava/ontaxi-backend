package vn.ontaxi.common.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.ontaxi.common.constant.EmailType;
import vn.ontaxi.common.jpa.entity.EmailTemplate;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {

    EmailTemplate findByEmailType(EmailType emailType);

}
