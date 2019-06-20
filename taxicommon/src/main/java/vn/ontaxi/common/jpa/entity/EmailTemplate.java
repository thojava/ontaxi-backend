package vn.ontaxi.common.jpa.entity;

import vn.ontaxi.common.constant.EmailType;

import javax.persistence.*;

@Entity
public class EmailTemplate extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String subject;
    @Lob
    private String emailContent;
    private EmailType emailType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getEmailContent() {
        return emailContent;
    }

    public void setEmailContent(String emailContent) {
        this.emailContent = emailContent;
    }

    public EmailType getEmailType() {
        return emailType;
    }

    public void setEmailType(EmailType emailType) {
        this.emailType = emailType;
    }

    @Override
    public String getKey() {
        return getId() + "";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EmailTemplate) {
            EmailTemplate that = (EmailTemplate) obj;

            if (that.getId() == null || id == null)
                return false;
            return this.getId().equals(that.getId());
        }
        return false;
    }

    @Override
    public String toString() {
        return "EmailTemplate{" +
                "id=" + id +
                ", name='" + subject + '\'' +
                '}';
    }
}
