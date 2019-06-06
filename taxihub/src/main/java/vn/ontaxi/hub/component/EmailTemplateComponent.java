package vn.ontaxi.hub.component;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
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
    private List<EmailTemplate> lstEmailTemplates;
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

    public List<EmailTemplate> getLstEmailTemplates() {

        if (lstEmailTemplates == null)
            lstEmailTemplates = emailTemplateRepository.findAll(new Sort(Sort.Direction.DESC, "createdDatetime"));

        return lstEmailTemplates;
    }

    public void deleteTemplates() {
        List<EmailTemplate> deletedTemplates = lstEmailTemplates.stream().filter(email -> email.isBeanSelected()).collect(Collectors.toList());
        emailTemplateRepository.delete(deletedTemplates);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", String.format("Đã xóa %s email mẫu", deletedTemplates.size())));
        lstEmailTemplates = null;
    }

    public String createNewTemplate() {
        EmailTemplate emailTemplate = new EmailTemplate();
        EmailTemplate save = emailTemplateRepository.save(emailTemplate);
        return "email_template_detail.jsf?faces-redirect=true&id=" + save.getId();
    }

    public void saveTemplate() {
        emailTemplateRepository.save(currentEmailTemplate);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Đã lưu"));
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
