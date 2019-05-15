package vn.ontaxi.hub.service.user;

import vn.ontaxi.common.jpa.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserByUserName(String username);
}
