package vn.ontaxi.common.jpa.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class EmailScheduler extends AbstractEntity {

    public enum RECURRING_TYPE {
        NONE("Không lặp"),
        DAY("Hàng ngày"),
        WEEK("Hàng tuần"),
        MONTH("Hàng tháng"),
        YEAR("Hàng năm");

        private String name;

        RECURRING_TYPE(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinColumn(name = "email_template_id")
    private EmailTemplate emailTemplate;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinColumn(name = "customer_group_id")
    private CustomerGroup customerGroup;

    private boolean multipleTimePerCustomer;
    private boolean enable = true;

    private Date startTime;
    private Date endTime;

    @Enumerated(EnumType.STRING)
    private RECURRING_TYPE recurringType;
    private String cronJob;

    public String getCronJob() {
        return cronJob;
    }

    public void setCronJob(String cronJob) {
        this.cronJob = cronJob;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EmailTemplate getEmailTemplate() {
        return emailTemplate;
    }

    @Transient
    public boolean getCanChangeState() {
        if (endTime == null)
            return true;
        Date currentDate = new Date();
        return currentDate.before(endTime);
    }

    public void setEmailTemplate(EmailTemplate emailTemplate) {
        this.emailTemplate = emailTemplate;
    }

    public CustomerGroup getCustomerGroup() {
        return customerGroup;
    }

    public void setCustomerGroup(CustomerGroup customerGroup) {
        this.customerGroup = customerGroup;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public RECURRING_TYPE getRecurringType() {
        return recurringType;
    }

    public void setRecurringType(RECURRING_TYPE recurringType) {
        this.recurringType = recurringType;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean isMultipleTimePerCustomer() {
        return multipleTimePerCustomer;
    }

    public void setMultipleTimePerCustomer(boolean multipleTimePerCustomer) {
        this.multipleTimePerCustomer = multipleTimePerCustomer;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public boolean equals(Object obj) {

        EmailScheduler that = (EmailScheduler) obj;

        if (that.getId() == null || id == null)
            return false;
        return this.getId().equals(that.getId());
    }

    @Override
    public String getKey() {
        return id + "";
    }
}
