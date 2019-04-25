package vn.ontaxi.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ontaxi.jpa.entity.User;
import vn.ontaxi.jpa.repository.UserRepository;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> getUserByUserName(String username) {
        return Optional.ofNullable(userRepository.findOne(username));
    }
}
