```java
package com.example.scholarhaven.controller.api;

import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    public ResponseEntity<List<Map<String, Object>>> getAllUsers(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("========== ADMIN GET ALL USERS ==========");
        System.out.println("Admin: " + (userDetails != null ? userDetails.getUsername() : "null"));
        
        List<Map<String, Object>> userDtos = userService.findAllUsers().stream().map(user -> Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "enabled", user.getEnabled(),
                "roles", user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toList())
        )).collect(Collectors.toList());

        System.out.println("Returning " + userDtos.size() + " users");
        return ResponseEntity.ok(userDtos);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, 
                                        @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("========== ADMIN DELETE USER ==========");
        System.out.println("Admin: " + (userDetails != null ? userDetails.getUsername() : "null"));
        System.out.println("User ID to delete: " + id);
        
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        
        try {
            User currentUser = userService.findByUsername(userDetails.getUsername());
            if (currentUser.getId().equals(id)) {
                return ResponseEntity.badRequest().body(Map.of("error", "You cannot delete your own account"));
            }
            
            userService.deleteUserById(id);
            System.out.println("User deleted successfully: ID " + id);
            return ResponseEntity.noContent().build();
            
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }

    @PostMapping("/users/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> disableUser(@PathVariable Long id, 
                                         @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("========== ADMIN DISABLE USER ==========");
        System.out.println("Admin: " + (userDetails != null ? userDetails.getUsername() : "null"));
        System.out.println("User ID to disable: " + id);
        
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        
        try {
            userService.disableUserById(id);
            return ResponseEntity.ok().body(Map.of("message", "User disabled successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/users/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> enableUser(@PathVariable Long id, 
                                        @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("========== ADMIN ENABLE USER ==========");
        System.out.println("Admin: " + (userDetails != null ? userDetails.getUsername() : "null"));
        System.out.println("User ID to enable: " + id);
        
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        
        try {
            userService.enableUserById(id);
            return ResponseEntity.ok().body(Map.of("message", "User enabled successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}