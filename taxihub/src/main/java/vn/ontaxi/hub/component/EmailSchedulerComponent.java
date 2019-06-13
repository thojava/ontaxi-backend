package vn.ontaxi.hub.component;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.jpa.entity.CustomerGroup;
import vn.ontaxi.common.jpa.entity.EmailScheduler;
import vn.ontaxi.common.jpa.repository.EmailScheduleRepository;
import vn.ontaxi.hub.service.EmailSchedulerService;
import vn.ontaxi.hub.utils.CronJobUtils;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.List;

@Component
@Scope("view")
public class EmailSchedulerComponent {
    private final EmailScheduleRepository emailScheduleRepository;
    private final EmailSchedulerService emailSchedulerService;

    private List<EmailScheduler> lstEmailSchedulers;
    private EmailScheduler currentEmailScheduler;

    @Autowired
    public EmailSchedulerComponent(EmailScheduleRepository emailScheduleRepository, EmailSchedulerService emailSchedulerService) {
        this.emailScheduleRepository = emailScheduleRepository;
        this.emailSchedulerService = emailSchedulerService;
        currentEmailScheduler = new EmailScheduler();
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
        lstEmailSchedulers = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", isUpdate ? "Cập nhật thành công" : "Lên lịch gửi email thành công"));
        try {
            emailSchedulerService.addOrUpdateEmailScheduler(saved);
        } catch (SchedulerException e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Không thể lên lịch chạy với cấu hình hiện tại"));
        }
    }

    public void toggleEmailSchedulerState(EmailScheduler emailScheduler) throws SchedulerException {
        if (emailScheduler.isEnable()) {
            try {
                emailSchedulerService.resumeEmailScheduler(emailScheduler);
            } catch (SchedulerException e) {
                e.printStackTrace();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Không thể lên lịch chạy với cấu hình hiện tại"));
            }
        } else {
            emailSchedulerService.pauseEmailScheduler(emailScheduler);
        }
    }

    public void deleteEmailScheduler() throws SchedulerException {
        for (EmailScheduler emailScheduler : lstEmailSchedulers) {
            if (emailScheduler.isBeanSelected()) {
                emailSchedulerService.deleteEmailScheduler(emailScheduler);
                emailScheduleRepository.delete(emailScheduler);
            }
        }

        lstEmailSchedulers = null;
    }

    public EmailScheduler getCurrentEmailScheduler() {
        return currentEmailScheduler;
    }

    public void setCurrentEmailScheduler(EmailScheduler currentEmailScheduler) {
        this.currentEmailScheduler = currentEmailScheduler;
    }

    public void setLstEmailSchedulers(List<EmailScheduler> lstEmailSchedulers) {
        this.lstEmailSchedulers = lstEmailSchedulers;
    }

}
