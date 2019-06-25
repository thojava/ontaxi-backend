package vn.ontaxi.common.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class User extends AbstractEntity {
    @Id
    private String userName;
    @JsonIgnore
    private String password;
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(length = 1000)
    private String stringeeAccessToken;

    public User() {
    }

    public String getStringeeAccessToken() {
        return stringeeAccessToken;
    }

    public void setStringeeAccessToken(String stringeeAccessToken) {
        this.stringeeAccessToken = stringeeAccessToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    @JsonIgnore
    public String getKey() {
        return null;
    }

    @Transient
    public boolean isHelpDesk() {
        return role.equals(Role.ROLE_HELPDESK);
    }
}
