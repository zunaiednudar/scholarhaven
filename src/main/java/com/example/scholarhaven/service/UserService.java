package com.example.scholarhaven.service;

import com.example.scholarhaven.entity.Role;
import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.entity.Book;
import com.example.scholarhaven.entity.Order;
import com.example.scholarhaven.repository.BookRepository;
import com.example.scholarhaven.repository.OrderRepository;
import com.example.scholarhaven.repository.RoleRepository;
import com.example.scholarhaven.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;
    
    @PersistenceContext
    private EntityManager entityManager;

    // REGISTRATION

    @Transactional
    public User registerNewUser(User user) {
        System.out.println("========== REGISTERING NEW USER ==========");
        System.out.println("Name: " + user.getName());
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());

        if (userRepository.existsByUsername(user.getUsername())) {
            System.out.println("Username already exists: " + user.getUsername());
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            System.out.println("Email already exists: " + user.getEmail());
            throw new RuntimeException("Email already exists");
        }

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            System.out.println("Name is required");
            throw new RuntimeException("Name is required");
        }

        System.out.println("Encoding password...");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        System.out.println("Looking for BUYER role...");
        Role buyerRole = roleRepository.findByName("BUYER")
                .orElseThrow(() -> new RuntimeException("BUYER role not found"));

        user.addRole(buyerRole);
        System.out.println("BUYER role assigned");

        System.out.println("💾 Saving user to database...");
        User savedUser = userRepository.save(user);
        System.out.println("User saved with ID: " + savedUser.getId());
        System.out.println("\n");

        return savedUser;
    }

    // FIND USERS

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // ADMIN HELPER

    @Transactional
    public User getOrCreateAdmin(String username, String rawPassword) {
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

        if (userRepository.existsByUsername(username)) {
            User existing = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Admin user not found after exists check"));

            boolean hasAdmin = existing.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()));
            if (!hasAdmin) {
                existing.getRoles().add(adminRole);
                userRepository.save(existing);
            }
            return existing;
        }

        User admin = new User();
        admin.setName("Administrator");
        admin.setUsername(username);
        admin.setEmail(username + "@scholarhaven.local");
        admin.setPassword(passwordEncoder.encode(rawPassword));
        admin.setEnabled(true);
        admin.setCreatedAt(LocalDateTime.now());
        admin.getRoles().add(adminRole);

        return userRepository.save(admin);
    }

    // ROLE MANAGEMENT

    @Transactional
    public void addRoleToUser(String username, String roleName) {
        System.out.println("========== ADDING ROLE ==========");
        System.out.println("Username: " + username);
        System.out.println("Role to add: " + roleName);

        User user = findByUsername(username);
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        if (user.hasRole(roleName)) {
            System.out.println("User already has role: " + roleName);
            return;
        }

        user.addRole(role);
        userRepository.save(user);
        System.out.println("Added role " + roleName + " to user " + username);
        System.out.println("User now has roles: " + user.getRoles().stream()
                .map(Role::getName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("none"));
        System.out.println("\n");
    }

    @Transactional
    public void removeRoleFromUser(String username, String roleName) {
        System.out.println("========== REMOVING ROLE ==========");
        System.out.println("Username: " + username);
        System.out.println("Role to remove: " + roleName);

        User user = findByUsername(username);
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        if (!user.hasRole(roleName)) {
            System.out.println("User does not have role: " + roleName);
            return;
        }

        user.removeRole(role);
        userRepository.save(user);
        System.out.println("Removed role " + roleName + " from user " + username);
        System.out.println("User now has roles: " + user.getRoles().stream()
                .map(Role::getName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("none"));
        System.out.println("\n");
    }

    @Transactional
    public void makeUserSeller(String username) {
        addRoleToUser(username, "SELLER");
    }

    @Transactional
    public void removeUserSeller(String username) {
        removeRoleFromUser(username, "SELLER");
    }

    public boolean isUserSeller(String username) {
        User user = findByUsername(username);
        return user.hasRole("SELLER");
    }

    public boolean isUserBuyer(String username) {
        User user = findByUsername(username);
        return user.hasRole("BUYER");
    }

    public boolean isUserAdmin(String username) {
        User user = findByUsername(username);
        return user.hasRole("ADMIN");
    }

    public Set<Role> getUserRoles(String username) {
        User user = findByUsername(username);
        return user.getRoles();
    }

    // USER MANAGEMENT

    @Transactional
    public void enableUser(String username) {
        User user = findByUsername(username);
        user.setEnabled(true);
        userRepository.save(user);
        System.out.println("Enabled user: " + username);
    }

    @Transactional
    public void enableUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setEnabled(true);
        userRepository.save(user);
        System.out.println("Enabled user ID: " + id);
    }

    @Transactional
    public void disableUser(String username) {
        User user = findByUsername(username);
        user.setEnabled(false);
        userRepository.save(user);
        System.out.println("Disabled user: " + username);
    }

    @Transactional
    public void disableUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setEnabled(false);
        userRepository.save(user);
        System.out.println("Disabled user ID: " + id);
    }

    @Transactional
    public void deleteUserByUsername(String username) {
        System.out.println("========== DELETE USER BY USERNAME ==========");
        System.out.println("Username: " + username);
        
        User user = findByUsername(username);
        deleteUserWithRelations(user);
    }

    @Transactional
    public void deleteUserById(Long id) {
        System.out.println("========== DELETE USER BY ID ==========");
        System.out.println("User ID: " + id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        deleteUserWithRelations(user);
    }

    /**
     * Helper method to delete user and all related records using direct SQL for guaranteed deletion
     */
    private void deleteUserWithRelations(User user) {
        System.out.println("Deleting user: " + user.getUsername() + " (ID: " + user.getId() + ")");
        
        try {
            // 1. Delete password reset tokens (CRITICAL - was missing!)
            System.out.println("🗑️ Deleting password reset tokens...");
            int tokensDeleted = entityManager.createNativeQuery(
                "DELETE FROM password_reset_tokens WHERE user_id = ?")
                .setParameter(1, user.getId())
                .executeUpdate();
            System.out.println("   Deleted " + tokensDeleted + " password reset tokens");
            
            // 2. Delete order_items
            System.out.println("🗑️ Deleting order items for user's orders...");
            int orderItemsDeleted = entityManager.createNativeQuery(
                "DELETE FROM order_items WHERE order_id IN (SELECT id FROM orders WHERE buyer_id = ?)")
                .setParameter(1, user.getId())
                .executeUpdate();
            System.out.println("   Deleted " + orderItemsDeleted + " order items");
            
            // 3. Delete orders
            System.out.println("🗑️ Deleting orders for user...");
            int ordersDeleted = entityManager.createNativeQuery(
                "DELETE FROM orders WHERE buyer_id = ?")
                .setParameter(1, user.getId())
                .executeUpdate();
            System.out.println("   Deleted " + ordersDeleted + " orders");
            
            // 4. Delete books
            System.out.println("🗑️ Deleting books owned by user...");
            int booksDeleted = entityManager.createNativeQuery(
                "DELETE FROM books WHERE seller_id = ?")
                .setParameter(1, user.getId())
                .executeUpdate();
            System.out.println("   Deleted " + booksDeleted + " books");
            
            // 5. Delete user roles
            System.out.println("🗑️ Deleting user roles...");
            int rolesDeleted = entityManager.createNativeQuery(
                "DELETE FROM user_roles WHERE user_id = ?")
                .setParameter(1, user.getId())
                .executeUpdate();
            System.out.println("   Deleted " + rolesDeleted + " user-role associations");
            
            // 6. Finally delete the user
            System.out.println("🗑️ Deleting user...");
            int userDeleted = entityManager.createNativeQuery(
                "DELETE FROM users WHERE id = ?")
                .setParameter(1, user.getId())
                .executeUpdate();
            System.out.println("   User deleted: " + (userDeleted > 0));
            
            entityManager.flush();
            
            System.out.println("User and all related data deleted successfully");
            System.out.println("\n");
            
        } catch (Exception e) {
            System.out.println("Error during deletion: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void updateUserEmail(String username, String newEmail) {
        if (userRepository.existsByEmail(newEmail)) {
            throw new RuntimeException("Email already in use: " + newEmail);
        }

        User user = findByUsername(username);
        user.setEmail(newEmail);
        userRepository.save(user);
        System.out.println("Updated email for user: " + username);
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = findByUsername(username);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        System.out.println("Password changed for user: " + username);
    }

    @Transactional
    public void resetPassword(String username, String newPassword) {
        User user = findByUsername(username);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        System.out.println("Password reset for user: " + username);
    }

    // PASSWORD RESET EMAIL

    public void sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found: " + email));

        System.out.println("📧 Password reset email would be sent to: " + email);
        System.out.println("   User: " + user.getUsername());
        System.out.println("   Reset link: http://localhost:8080/reset-password?token=PLACEHOLDER_TOKEN");
    }

    // STATISTICS

    public long getTotalUserCount() {
        return userRepository.count();
    }

    public long getBuyerCount() {
        Role buyerRole = roleRepository.findByName("BUYER").orElse(null);
        if (buyerRole == null) return 0;
        return buyerRole.getUsers().size();
    }

    public long getSellerCount() {
        Role sellerRole = roleRepository.findByName("SELLER").orElse(null);
        if (sellerRole == null) return 0;
        return sellerRole.getUsers().size();
    }

    public long getAdminCount() {
        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
        if (adminRole == null) return 0;
        return adminRole.getUsers().size();
    }

    // DEBUG METHODS

    public void printAllUsers() {
        System.out.println("\n========== ALL USERS ==========");
        List<User> users = userRepository.findAll();
        for (User user : users) {
            System.out.println("ID: " + user.getId());
            System.out.println("  Name: " + user.getName());
            System.out.println("  Username: " + user.getUsername());
            System.out.println("  Email: " + user.getEmail());
            System.out.println("  Enabled: " + user.getEnabled());
            System.out.println("  Created At: " + user.getCreatedAt());
            System.out.println("  Roles: " + user.getRoles().stream()
                    .map(Role::getName)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("none"));
        }
        System.out.println("\n");
    }

    public void printUserDetails(String username) {
        try {
            User user = findByUsername(username);
            System.out.println("\n========== USER DETAILS ==========");
            System.out.println("ID: " + user.getId());
            System.out.println("Name: " + user.getName());
            System.out.println("Username: " + user.getUsername());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Enabled: " + user.getEnabled());
            System.out.println("Created At: " + user.getCreatedAt());
            System.out.println("Roles: " + user.getRoles().stream()
                    .map(Role::getName)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("none"));
            System.out.println("Has SELLER role: " + user.hasRole("SELLER"));
            System.out.println("Has BUYER role: " + user.hasRole("BUYER"));
            System.out.println("Has ADMIN role: " + user.hasRole("ADMIN"));
            System.out.println("\n");
        } catch (Exception e) {
            System.out.println("User not found: " + username);
        }
    }
}