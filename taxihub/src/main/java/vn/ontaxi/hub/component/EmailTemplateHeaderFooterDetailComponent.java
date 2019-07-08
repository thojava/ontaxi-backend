package vn.ontaxi.hub.component;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.jpa.entity.EmailTemplateHeaderFooter;
import vn.ontaxi.common.jpa.repository.EmailTemplateHeaderFooterRepository;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.Map;

@Component
@Scope("view")
public class EmailTemplateHeaderFooterDetailComponent {

    private EmailTemplateHeaderFooter currentEmailHeaderFooter = new EmailTemplateHeaderFooter();
    private final EmailTemplateHeaderFooterRepository emailTemplateHeaderFooterRepository;

    public EmailTemplateHeaderFooterDetailComponent(EmailTemplateHeaderFooterRepository emailTemplateHeaderFooterRepository) {
        this.emailTemplateHeaderFooterRepository = emailTemplateHeaderFooterRepository;
    }

    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String templateId = params.get("id");
        if (StringUtils.isNotEmpty(templateId) && NumberUtils.isDigits(templateId)) {
            currentEmailHeaderFooter = emailTemplateHeaderFooterRepository.findOne(Long.parseLong(templateId));
        }
    }

    public void saveEmailHeaderFooter() {
        currentEmailHeaderFooter = emailTemplateHeaderFooterRepository.saveAndFlush(currentEmailHeaderFooter);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Đã lưu"));
    }

    public EmailTemplateHeaderFooter getCurrentEmailHeaderFooter() {
        return currentEmailHeaderFooter;
    }

    public void setCurrentEmailHeaderFooter(EmailTemplateHeaderFooter currentEmailHeaderFooter) {
        this.currentEmailHeaderFooter = currentEmailHeaderFooter;
    }
}
