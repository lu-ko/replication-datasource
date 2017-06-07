package sk.elko.demo.routing.repository;

import sk.elko.demo.routing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

}
