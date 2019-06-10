package vn.ontaxi.common.jpa.entity;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;

@Entity
public class EmailTemplate extends AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String code;
    private String emailContent;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmailContent() {
        return emailContent;
    }

    public void setEmailContent(String emailContent) {
        this.emailContent = emailContent;
    }

    /*public Type getEmailType() {
        return emailType;
    }

    public void setEmailType(Type emailType) {
        this.emailType = emailType;
    }*/

    @Transient
    public boolean isCanDelete() {
        return !StringUtils.isNotEmpty(code);
    }

    @Override
    public String getKey() {
        return getId() + "";
    }

    @Override
    public boolean equals(Object obj) {

        EmailTemplate that = (EmailTemplate) obj;

        if (that.getId() == null || id == null)
            return false;
        return this.getId().equals(that.getId());
    }

    @Override
    public String toString() {
        return "EmailTemplate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
