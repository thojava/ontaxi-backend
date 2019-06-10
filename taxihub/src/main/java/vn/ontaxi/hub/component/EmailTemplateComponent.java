package vn.ontaxi.hub.component;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.jpa.entity.CustomerGroup;
import vn.ontaxi.common.jpa.entity.EmailScheduler;
import vn.ontaxi.common.jpa.entity.EmailTemplate;
import vn.ontaxi.common.jpa.repository.CustomerGroupRepository;
import vn.ontaxi.common.jpa.repository.EmailScheduleRepository;
import vn.ontaxi.common.jpa.repository.EmailTemplateRepository;
import vn.ontaxi.hub.service.EmailSchedulerService;
import vn.ontaxi.hub.utils.CronJobUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Scope("view")
public class EmailTemplateComponent {

    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailScheduleRepository emailScheduleRepository;
    private final CustomerGroupRepository customerGroupRepository;
    private final EmailSchedulerService emailSchedulerService;
    private List<EmailTemplate> lstEmailTemplates;
    private List<EmailScheduler> lstEmailSchedulers;
    private List<CustomerGroup> lstCustomerGroups;
    private EmailTemplate currentEmailTemplate;
    private EmailScheduler currentEmailScheduler;

    public EmailTemplateComponent(EmailTemplateRepository emailTemplateRepository, EmailScheduleRepository emailScheduleRepository, CustomerGroupRepository customerGroupRepository, EmailSchedulerService emailSchedulerService) {
        this.emailTemplateRepository = emailTemplateRepository;
        this.emailScheduleRepository = emailScheduleRepository;
        this.customerGroupRepository = customerGroupRepository;
        this.emailSchedulerService = emailSchedulerService;
        currentEmailTemplate = new EmailTemplate();
        currentEmailScheduler = new EmailScheduler();
    }

    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String templateId = params.get("id");
        if (StringUtils.isNotEmpty(templateId) && NumberUtils.isDigits(templateId)) {
            currentEmailTemplate = emailTemplateRepository.findOne(Long.parseLong(templateId));
        }

        lstCustomerGroups = customerGroupRepository.findAll(new Sort(Sort.Direction.ASC, "name"));
    }

    public List<EmailTemplate> getLstEmailTemplates() {

        if (lstEmailTemplates == null)
            lstEmailTemplates = emailTemplateRepository.findAll(new Sort(Sort.Direction.ASC, "name"));

        return lstEmailTemplates;
    }

    public void deleteTemplates() {
        try {
            List<EmailTemplate> deletedTemplates = lstEmailTemplates.stream().filter(email -> email.isBeanSelected()).collect(Collectors.toList());
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

    public void saveTemplate() {
        emailTemplateRepository.save(currentEmailTemplate);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Đã lưu"));
    }

    public List<CustomerGroup> getLstCustomerGroups() {
        return lstCustomerGroups;
    }

    public void setLstCustomerGroups(List<CustomerGroup> lstCustomerGroups) {
        this.lstCustomerGroups = lstCustomerGroups;
    }

    public List<EmailScheduler> getLstEmailSchedulers() {

        if (lstEmailSchedulers == null)
            lstEmailSchedulers = emailScheduleRepository.findAll(new Sort(Sort.Direction.DESC, "createdDatetime"));

        return lstEmailSchedulers;
    }

    public void openDialogCreateNewEmailSchedule() {
        currentEmailScheduler = new EmailScheduler();
    }

    public void saveEmailScheduler() {
        boolean isUpdate = currentEmailScheduler.getId() != null;
        currentEmailScheduler.setCronJob(CronJobUtils.buildCronJobs(currentEmailScheduler.getStartTime(), currentEmailScheduler.getRecurringType()));
        EmailScheduler saved = emailScheduleRepository.save(currentEmailScheduler);
        emailSchedulerService.scheduleEmail(saved);
        lstEmailSchedulers = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", isUpdate ? "Cập nhật thành công" : "Lên lịch gửi email thành công"));
    }

    public void toggleEmailSchedulerState(EmailScheduler emailScheduler) {
        if (emailScheduler.isEnable()) {
            emailSchedulerService.scheduleEmail(emailScheduler);
        } else {
            emailSchedulerService.stopAndRemoveSchedule(emailScheduler.getKey());
        }
    }

    public void deleteEmailScheduler() {
        for (EmailScheduler emailScheduler : lstEmailSchedulers) {
            if (emailScheduler.isBeanSelected()) {
                emailSchedulerService.stopAndRemoveSchedule(emailScheduler.getKey());
                emailScheduleRepository.delete(emailScheduler);
            }
        }

        lstEmailSchedulers = null;
    }

    public void setLstEmailSchedulers(List<EmailScheduler> lstEmailSchedulers) {
        this.lstEmailSchedulers = lstEmailSchedulers;
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

    public EmailScheduler getCurrentEmailScheduler() {
        return currentEmailScheduler;
    }

    public void setCurrentEmailScheduler(EmailScheduler currentEmailScheduler) {
        this.currentEmailScheduler = currentEmailScheduler;
    }
}
