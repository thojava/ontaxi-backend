package vn.ontaxi.common.jpa.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.ontaxi.common.constant.EmailType;
import vn.ontaxi.common.jpa.entity.EmailTemplate;

import java.util.List;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {

    EmailTemplate findByEmailType(EmailType emailType);
    List<EmailTemplate> findByEmailTypeIn(Sort var1, EmailType... emailTypes);
    List<EmailTemplate> findByEmailTypeNotIn(Sort var1, EmailType... emailTypes);
}
