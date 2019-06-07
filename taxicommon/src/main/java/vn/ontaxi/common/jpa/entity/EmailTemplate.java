package vn.ontaxi.common.jpa.entity;

import javax.persistence.*;

@Entity
public class EmailTemplate extends AbstractEntity{

    /*public enum Type {
        WELCOME("Welcome"),
        POST_ORDER("Post Order"),
        ORDER_SUCCESS("Order Success");

        private String name;

        Type(String name) {
            this.name = name;
        }

        public String getName(){
            return name;
        }
    }*/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String emailContent;
    //@Enumerated(EnumType.STRING)
    //private Type emailType;

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
