package com.diver.repository;

import com.diver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional< User>  findByEmail(String username);

}