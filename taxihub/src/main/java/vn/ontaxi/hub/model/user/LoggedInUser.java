package vn.ontaxi.hub.model.user;

import org.springframework.security.core.authority.AuthorityUtils;
import vn.ontaxi.common.jpa.entity.User;

public class LoggedInUser extends org.springframework.security.core.userdetails.User {
    private User user;

    public LoggedInUser(User user) {
        super(user.getUserName(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().toString()));
        this.user = user;
    }

    public User getUser() {
        return user;
    }

}
