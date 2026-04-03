package com.example.scholarhaven.config;

import com.example.scholarhaven.entity.Category;
import com.example.scholarhaven.entity.Role;
import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.repository.CategoryRepository;
import com.example.scholarhaven.repository.RoleRepository;
import com.example.scholarhaven.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("\nDATA INITIALIZER STARTED");

        // ========== CREATE DEFAULT ROLES ==========
        if (roleRepository.count() == 0) {
            System.out.println("Creating default roles...");

            Role adminRole = new Role();
            adminRole.setName("ADMIN");

            Role sellerRole = new Role();
            sellerRole.setName("SELLER");

            Role buyerRole = new Role();
            buyerRole.setName("BUYER");

            roleRepository.save(adminRole);
            roleRepository.save(sellerRole);
            roleRepository.save(buyerRole);

            System.out.println("Default roles created: ADMIN, SELLER, BUYER");
        } else {
            System.out.println("Roles already exist, found: " + roleRepository.count() + " roles");
        }

        // CREATE/UPDATE ADMIN USER
        System.out.println("👑 Checking admin user...");
        
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
        
        User admin = userRepository.findByUsername("admin").orElse(null);
        
        if (admin == null) {
            // Create new admin
            admin = new User();
            admin.setName("Administrator");
            admin.setUsername("admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEnabled(true);
            admin.addRole(adminRole);
            userRepository.save(admin);
            System.out.println("Admin user created - Username: admin, Password: admin123");
        } else {
            // Update existing admin's password to ensure it's correct
            admin.setName("Administrator");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEnabled(true);
            
            // Ensure admin has ADMIN role
            if (!admin.hasRole("ADMIN")) {
                admin.addRole(adminRole);
            }
            
            userRepository.save(admin);
            System.out.println("Admin user updated - Password reset to: admin123");
        }

        // ========== CREATE DEFAULT CATEGORIES ==========
        if (categoryRepository.count() == 0) {
            System.out.println("Creating default categories...");

            Category[] categories = {
                    createCategory("Fiction", "Fictional books, novels, and literature"),
                    createCategory("Non-Fiction", "Educational, biographical, and factual books"),
                    createCategory("Science", "Science, physics, chemistry, biology books"),
                    createCategory("Technology", "Programming, IT, engineering, and tech books"),
                    createCategory("Children's Books", "Books for children and young readers"),
                    createCategory("Academic", "Textbooks, academic publications, research materials"),
                    createCategory("History", "Historical books and biographies"),
                    createCategory("Self-Help", "Personal development and self-help books"),
                    createCategory("Business", "Business, economics, and entrepreneurship"),
                    createCategory("Art", "Art, music, photography, and design books")
            };

            for (Category category : categories) {
                categoryRepository.save(category);
                System.out.println("  - Created: " + category.getName());
            }

            System.out.println(categories.length + " default categories created successfully!");
        } else {
            System.out.println("Categories already exist, found: " + categoryRepository.count() + " categories");
        }

        System.out.println("DATA INITIALIZER COMPLETED\n");
    }

    private Category createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return category;
    }
}