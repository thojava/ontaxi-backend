package vn.ontaxi.hub.component;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.jpa.entity.AbstractEntity;
import vn.ontaxi.common.jpa.entity.EmailTemplate;
import vn.ontaxi.common.jpa.repository.CustomerGroupRepository;
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
    private final CustomerGroupRepository customerGroupRepository;
    private List<EmailTemplate> lstEmailTemplates;

    private EmailTemplate currentEmailTemplate;

    public EmailTemplateComponent(EmailTemplateRepository emailTemplateRepository, CustomerGroupRepository customerGroupRepository) {
        this.emailTemplateRepository = emailTemplateRepository;
        this.customerGroupRepository = customerGroupRepository;
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

    public List<EmailTemplate> getLstEmailTemplates() {

        if (lstEmailTemplates == null)
            lstEmailTemplates = emailTemplateRepository.findAll(new Sort(Sort.Direction.ASC, "subject"));

        return lstEmailTemplates;
    }

    public void deleteTemplates() {
        try {
            List<EmailTemplate> deletedTemplates = lstEmailTemplates.stream().filter(AbstractEntity::isBeanSelected).collect(Collectors.toList());
            emailTemplateRepository.delete(deletedTemplates);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", String.format("Đã xóa %s email mẫu", deletedTemplates.size())));
            lstEmailTemplates = null;
        } catch (DataIntegrityViolationException ex) {
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Lỗi tham chiếu trong sql"));
        }
    }

    public String createNewTemplate() {
        EmailTemplate emailTemplate = new EmailTemplate();
        EmailTemplate save = emailTemplateRepository.save(emailTemplate);
        return "email_template_detail.jsf?faces-redirect=true&id=" + save.getId();
    }

    public String saveTemplate() {
        emailTemplateRepository.save(currentEmailTemplate);
        return "email_template.jsf?faces-redirect=true";
    }

    public void setLstEmailTemplates(List<EmailTemplate> lstEmailTemplates) {
        this.lstEmailTemplates = lstEmailTemplates;
    }

    public EmailTemplate getCurrentEmailTemplate() {
        return currentEmailTemplate;
    }

    public void setCurrentEmailTemplate(EmailTemplate currentEmailTemplate) {
        this.currentEmailTemplate = currentEmailTemplate;
    }

}
