package vn.ontaxi.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.ontaxi.jpa.entity.User;
import vn.ontaxi.model.user.LoggedInUser;

@Service
public class CurrentUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Autowired
    public CurrentUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email=%s was not found", username)));

        return new LoggedInUser(user);
    }
}
