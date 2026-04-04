package com.example.scholarhaven.repository;

import com.example.scholarhaven.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    // Returns Optional<Role> so I can handle the case where no role is found
    // SELECT * FROM roles WHERE name = ?;
    Optional<Role> findByName(String name);
}
