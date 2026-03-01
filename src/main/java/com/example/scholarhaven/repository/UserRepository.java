package com.example.scholarhaven.repository;

import com.example.scholarhaven.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// JpaRepository<User, Long> manages the User entity, primary key is of type Long
public interface UserRepository extends JpaRepository<User, Long> {
    // Returns Optional<User> so I can handle the case where no user is found
    // SELECT * FROM users WHERE username = ?;
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
