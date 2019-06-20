package vn.ontaxi.hub.component;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.constant.EmailType;
import vn.ontaxi.common.jpa.entity.AbstractEntity;
import vn.ontaxi.common.jpa.entity.EmailTemplate;
import vn.ontaxi.common.jpa.repository.EmailTemplateRepository;

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

    private List<EmailTemplate> marketingEmailTemplates;
    private List<EmailTemplate> systemEmailTemplates;
    private EmailTemplate currentEmailTemplate;

    public EmailTemplateComponent(EmailTemplateRepository emailTemplateRepository) {
        this.emailTemplateRepository = emailTemplateRepository;
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

    public List<EmailTemplate> getSystemEmailTemplates() {
        if (systemEmailTemplates == null)
            systemEmailTemplates = emailTemplateRepository.findByEmailTypeNotIn(new Sort(Sort.Direction.ASC, "subject"), EmailType.MARKETING);

        return systemEmailTemplates;
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

    public String createNewTemplate() {
        EmailTemplate emailTemplate = new EmailTemplate();
        emailTemplate.setEmailType(EmailType.MARKETING);
        EmailTemplate save = emailTemplateRepository.save(emailTemplate);
        return "email_template_detail.jsf?faces-redirect=true&id=" + save.getId();
    }

    public String saveTemplate() {
        emailTemplateRepository.save(currentEmailTemplate);
        return "email_template.jsf?faces-redirect=true";
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
