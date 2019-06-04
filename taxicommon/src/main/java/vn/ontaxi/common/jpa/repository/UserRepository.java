package vn.ontaxi.common.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import vn.ontaxi.common.jpa.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
}
