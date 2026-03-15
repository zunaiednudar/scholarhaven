package com.example.scholarhaven.controller.api;

import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> userDtos = userService.findAllUsers().stream().map(user -> Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "enabled", user.getEnabled(),
                "roles", user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toList())
        )).collect(Collectors.toList());

        return ResponseEntity.ok(userDtos);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> disableUser(@PathVariable Long id) {
        userService.disableUserById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> enableUser(@PathVariable Long id) {
        userService.enableUserById(id);
        return ResponseEntity.ok().build();
    }
}
