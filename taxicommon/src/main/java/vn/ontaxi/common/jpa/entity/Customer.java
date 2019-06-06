package vn.ontaxi.common.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.*;

@Entity
public class Customer extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    private String phone;
    private String name;
    private String email;
    private String job;
    private Date birthDay;
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Transient
    private Set<Behavior> behaviors;

    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "customer", cascade = CascadeType.ALL)
    @Fetch(FetchMode.SELECT)
    private Set<CustomerBehavior> customerBehaviors = new HashSet<>();

    public Set<CustomerBehavior> getCustomerBehaviors() {
        return customerBehaviors;
    }

    public void setCustomerBehaviors(Set<CustomerBehavior> customerBehaviors) {
        this.customerBehaviors = customerBehaviors;
    }

    public Set<Behavior> getBehaviors() {
        if (CollectionUtils.isEmpty(behaviors)) {
            behaviors = new TreeSet<>();
            for (CustomerBehavior customerBehavior : customerBehaviors) {
                behaviors.add(customerBehavior.getBehavior());
            }
        }

        return behaviors;
    }

    public void setBehaviors(Set<Behavior> behaviors) {
        this.behaviors = behaviors;
        this.customerBehaviors.clear();
        for (Behavior behavior : behaviors)
            this.customerBehaviors.add(new CustomerBehavior(this, behavior));
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @JsonIgnore
    public String getKey() {
        return String.valueOf(getId());
    }
}
