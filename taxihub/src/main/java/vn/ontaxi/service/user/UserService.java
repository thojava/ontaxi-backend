package vn.ontaxi.service.user;

import vn.ontaxi.jpa.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserByUserName(String username);
}
