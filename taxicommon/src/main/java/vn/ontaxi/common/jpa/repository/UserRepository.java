package vn.ontaxi.common.jpa.repository;

import org.springframework.data.repository.CrudRepository;
import vn.ontaxi.common.jpa.entity.User;

public interface UserRepository extends CrudRepository<User, String> {
}
