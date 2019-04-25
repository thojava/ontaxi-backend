package vn.ontaxi.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public abstract class AbstractEntity implements Serializable {
    private String createdBy;
    private Date createdDatetime;
    private Date lastUpdatedDatetime;
    @Version
    private Long version = 0L;

    @PrePersist
    public void onPrePersist() {
        createdDatetime = new Date();
        lastUpdatedDatetime = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        lastUpdatedDatetime = new Date();
    }

    @JsonIgnore
    public Date getCreated_datetime() {
        return createdDatetime;
    }

    public void setCreated_datetime(Date created_datetime) {
        this.createdDatetime = created_datetime;
    }

    @JsonIgnore
    public Date getLastUpdatedDatetime() {
        return lastUpdatedDatetime;
    }

    public void setLastUpdatedDatetime(Date last_updated_datetime) {
        this.lastUpdatedDatetime = last_updated_datetime;
    }

    @JsonIgnore
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Transient
    private boolean beanSelected;

    @JsonIgnore
    @Transient
    public boolean isBeanSelected() {
        return beanSelected;
    }

    public void setBeanSelected(boolean beanSelected) {
        this.beanSelected = beanSelected;
    }

    @JsonIgnore
    @Transient
    public abstract String getKey();
}
