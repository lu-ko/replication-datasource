package kr.pe.kwonnam.replicationdatasource.jpa.repository;

import kr.pe.kwonnam.replicationdatasource.jpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

}
