package vn.ontaxi.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity implements Serializable {
    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String modifiedBy;
    @CreatedDate
    private Date createdDatetime;
    @LastModifiedDate
    private Date lastUpdatedDatetime;
    @Version
    private Long version = 0L;

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

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
