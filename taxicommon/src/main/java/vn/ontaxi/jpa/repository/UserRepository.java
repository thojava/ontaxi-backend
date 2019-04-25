package vn.ontaxi.jpa.repository;

import org.springframework.data.repository.CrudRepository;
import vn.ontaxi.jpa.entity.User;

public interface UserRepository extends CrudRepository<User, String> {
}
