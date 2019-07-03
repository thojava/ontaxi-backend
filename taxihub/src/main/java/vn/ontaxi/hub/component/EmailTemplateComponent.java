package vn.ontaxi.hub.component;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.constant.EmailType;
import vn.ontaxi.common.jpa.entity.AbstractEntity;
import vn.ontaxi.common.jpa.entity.Customer;
import vn.ontaxi.common.jpa.entity.EmailTemplate;
import vn.ontaxi.common.jpa.entity.EmailTemplateHeaderFooter;
import vn.ontaxi.common.jpa.repository.CustomerRepository;
import vn.ontaxi.common.jpa.repository.EmailTemplateHeaderFooterRepository;
import vn.ontaxi.common.jpa.repository.EmailTemplateRepository;
import vn.ontaxi.common.service.EmailService;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Scope("view")
public class EmailTemplateComponent {
    private final EmailTemplateRepository emailTemplateRepository;
    private final CustomerRepository customerRepository;
    private final EmailService emailService;
    private final EmailTemplateHeaderFooterRepository emailTemplateHeaderFooterRepository;

    private List<EmailTemplate> marketingEmailTemplates;
    private List<EmailTemplate> marketingEmailTemplateHasTested;
    private List<EmailTemplate> systemEmailTemplates;
    private List<EmailTemplateHeaderFooter> emailTemplateHeaderFooters;
    private EmailTemplate currentEmailTemplate;

    public EmailTemplateComponent(EmailTemplateRepository emailTemplateRepository, CustomerRepository customerRepository, EmailService emailService, EmailTemplateHeaderFooterRepository emailTemplateHeaderFooterRepository) {
        this.emailTemplateRepository = emailTemplateRepository;
        this.customerRepository = customerRepository;
        this.emailService = emailService;
        this.emailTemplateHeaderFooterRepository = emailTemplateHeaderFooterRepository;
        currentEmailTemplate = new EmailTemplate();
    }

    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String templateId = params.get("id");
        if (StringUtils.isNotEmpty(templateId) && NumberUtils.isDigits(templateId)) {
            currentEmailTemplate = emailTemplateRepository.findOne(Long.parseLong(templateId));
        }
    }

    public List<EmailTemplate> getMarketingEmailTemplates() {
        if (marketingEmailTemplates == null)
            marketingEmailTemplates = emailTemplateRepository.findByEmailTypeIn(new Sort(Sort.Direction.ASC, "subject"), EmailType.MARKETING);

        return marketingEmailTemplates;
    }

    public List<EmailTemplate> getMarketingEmailTemplateHasTested() {
        if (marketingEmailTemplateHasTested == null)
            marketingEmailTemplateHasTested = emailTemplateRepository.findByEmailTypeInAndHasTestedTrueAndSubjectIsNotNull(new Sort(Sort.Direction.ASC, "subject"), EmailType.MARKETING);

        return marketingEmailTemplateHasTested;
    }

    public List<EmailTemplate> getSystemEmailTemplates() {
        if (systemEmailTemplates == null)
            systemEmailTemplates = emailTemplateRepository.findByEmailTypeNotIn(new Sort(Sort.Direction.ASC, "subject"), EmailType.MARKETING);

        return systemEmailTemplates;
    }

    public List<EmailTemplateHeaderFooter> getEmailTemplateHeaderFooters() {
        if (emailTemplateHeaderFooters == null)
            emailTemplateHeaderFooters = emailTemplateHeaderFooterRepository.findAll(new Sort(Sort.Direction.DESC, "active", "id"));
        return emailTemplateHeaderFooters;
    }

    public void deleteMarketingTemplates() {
        deleteTemplates(marketingEmailTemplates);
        marketingEmailTemplates = null;
    }

    public void deleteSystemTemplates() {
        deleteTemplates(systemEmailTemplates);
        systemEmailTemplates = null;
    }

    private void deleteTemplates(List<EmailTemplate> emailTemplates) {
        try {
            List<EmailTemplate> deletedTemplates = emailTemplates.stream().filter(AbstractEntity::isBeanSelected).collect(Collectors.toList());
            emailTemplateRepository.delete(deletedTemplates);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", String.format("Đã xóa %s email mẫu", deletedTemplates.size())));
        } catch (DataIntegrityViolationException ex) {
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Lỗi tham chiếu trong sql"));
        }
    }

    public void deleteEmailHeaderFooters() {
        List<EmailTemplateHeaderFooter> deleteHeaderFooters = emailTemplateHeaderFooters.stream().filter(AbstractEntity::isBeanSelected).collect(Collectors.toList());
        emailTemplateHeaderFooterRepository.delete(deleteHeaderFooters);
        emailTemplateHeaderFooters = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", String.format("Đã xóa %s mẫu header & footer", deleteHeaderFooters.size())));
    }

    public String createNewTemplate() {
        EmailTemplate emailTemplate = new EmailTemplate();
        emailTemplate.setEmailType(EmailType.MARKETING);
        EmailTemplate save = emailTemplateRepository.save(emailTemplate);
        return "email_template_detail.jsf?faces-redirect=true&id=" + save.getId();
    }

    public String createNewHeaderFooter() {
        EmailTemplateHeaderFooter emailTemplateHeaderFooter = new EmailTemplateHeaderFooter();
        EmailTemplateHeaderFooter saved = emailTemplateHeaderFooterRepository.saveAndFlush(emailTemplateHeaderFooter);
        return "email_header_footer.jsf?faces-redirect=true&id=" + saved.getId();
    }

    public void testSendEmail(EmailTemplate emailTemplate) {
        List<Customer> customers = customerRepository.findCustomersByTestedCustomerTrue();
        for (Customer customer : customers) {
            if (StringUtils.isNotEmpty(customer.getEmail()))
                emailService.sendEmail("Tested Email: " + emailTemplate.getSubject(), customer.getEmail(), emailTemplate.getEmailContent());
        }

        emailTemplate.setHasTested(true);
        if (emailTemplate.getId() != null)
            emailTemplateRepository.save(emailTemplate);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Đã gửi cho tested customer"));
    }

    public String saveTemplate() {
        emailTemplateRepository.save(currentEmailTemplate);
        return "email_template.jsf?faces-redirect=true";
    }

    public void changeActivatedHeaderFooter(Long id) {
        EmailTemplateHeaderFooter changedHeaderFooter = emailTemplateHeaderFooterRepository.findOne(id);
        List<EmailTemplateHeaderFooter> headerFooters = emailTemplateHeaderFooterRepository.findAll();
        headerFooters.forEach(headerFooter -> headerFooter.setActive(false));
        emailTemplateHeaderFooterRepository.save(headerFooters);

        changedHeaderFooter.setActive(!changedHeaderFooter.isActive());
        emailTemplateHeaderFooterRepository.save(changedHeaderFooter);

        emailTemplateHeaderFooters = null;

    }

    public void setEmailTemplateHeaderFooters(List<EmailTemplateHeaderFooter> emailTemplateHeaderFooters) {
        this.emailTemplateHeaderFooters = emailTemplateHeaderFooters;
    }

    public void setMarketingEmailTemplates(List<EmailTemplate> marketingEmailTemplates) {
        this.marketingEmailTemplates = marketingEmailTemplates;
    }

    public EmailTemplate getCurrentEmailTemplate() {
        return currentEmailTemplate;
    }

    public void setCurrentEmailTemplate(EmailTemplate currentEmailTemplate) {
        this.currentEmailTemplate = currentEmailTemplate;
    }

}
