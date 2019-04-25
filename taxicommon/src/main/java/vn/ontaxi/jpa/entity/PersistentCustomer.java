package vn.ontaxi.jpa.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PersistentCustomer extends AbstractEntity {
    @Id
    private String email;
    private String name;
    private boolean laterPaid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isLaterPaid() {
        return laterPaid;
    }

    public void setLaterPaid(boolean laterPaid) {
        this.laterPaid = laterPaid;
    }

    @Override
    public String getKey() {
        return getEmail();
    }
}
