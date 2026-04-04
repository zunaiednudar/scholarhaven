```java
package com.example.scholarhaven.controller;

import com.example.scholarhaven.entity.Book;
import com.example.scholarhaven.entity.Role;
import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.repository.BookRepository;
import com.example.scholarhaven.repository.RoleRepository;
import com.example.scholarhaven.repository.UserRepository;
import com.example.scholarhaven.security.JwtService;
import com.example.scholarhaven.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class ProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private User testUser;
    private User testSeller;
    private String userToken;
    private String sellerToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        Role buyerRole = roleRepository.findByName("BUYER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("BUYER");
                    return roleRepository.save(role);
                });

        Role sellerRole = roleRepository.findByName("SELLER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("SELLER");
                    return roleRepository.save(role);
                });

        testUser = new User();
        testUser.setName("Regular User");
        testUser.setUsername("regularuser");
        testUser.setEmail("regular@test.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setEnabled(true);
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(buyerRole);
        testUser.setRoles(userRoles);
        testUser = userRepository.save(testUser);
        
        UserPrincipal userPrincipal = new UserPrincipal(testUser);
        userToken = jwtService.generateToken(userPrincipal);

        testSeller = new User();
        testSeller.setName("Seller User");
        testSeller.setUsername("selleruser");
        testSeller.setEmail("seller@test.com");
        testSeller.setPassword(passwordEncoder.encode("password"));
        testSeller.setEnabled(true);
        Set<Role> sellerRoles = new HashSet<>();
        sellerRoles.add(buyerRole);
        sellerRoles.add(sellerRole);
        testSeller.setRoles(sellerRoles);
        testSeller = userRepository.save(testSeller);
        
        UserPrincipal sellerPrincipal = new UserPrincipal(testSeller);
        sellerToken = jwtService.generateToken(sellerPrincipal);

        Book book = new Book();
        book.setTitle("Seller's Book");
        book.setAuthor("Seller Author");
        book.setPrice(new BigDecimal("29.99"));
        book.setStock(5);
        book.setStatus(Book.BookStatus.AVAILABLE);
        book.setSeller(testSeller);
        bookRepository.save(book);
    }

    @Test
    void testProfile_AuthenticatedUser_ShouldReturnProfilePage() throws Exception {
        mockMvc.perform(get("/profile")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("rolesList"))
                .andExpect(model().attributeExists("bookCount"));
    }

    @Test
    void testProfile_AuthenticatedSeller_ShouldShowProfilePage() throws Exception {
        mockMvc.perform(get("/profile")
                .header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("bookCount"));
    }

    @Test
    void testProfile_Unauthenticated_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteAccountApi_RegularUser_ShouldSucceed() throws Exception {
        User freshUser = new User();
        freshUser.setName("Fresh User");
        freshUser.setUsername("freshuser");
        freshUser.setEmail("fresh@test.com");
        freshUser.setPassword(passwordEncoder.encode("password"));
        freshUser.setEnabled(true);
        Role buyerRole = roleRepository.findByName("BUYER").get();
        Set<Role> roles = new HashSet<>();
        roles.add(buyerRole);
        freshUser.setRoles(roles);
        freshUser = userRepository.save(freshUser);
        
        UserPrincipal freshPrincipal = new UserPrincipal(freshUser);
        String freshToken = jwtService.generateToken(freshPrincipal);

        mockMvc.perform(post("/api/profile/delete")
                .header("Authorization", "Bearer " + freshToken)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testDeleteAccountApi_AdminUser_ShouldFail() throws Exception {
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ADMIN");
                    return roleRepository.save(role);
                });

        User adminUser = new User();
        adminUser.setName("Admin User");
        adminUser.setUsername("adminuser");
        adminUser.setEmail("admin@test.com");
        adminUser.setPassword(passwordEncoder.encode("password"));
        adminUser.setEnabled(true);
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        adminUser.setRoles(adminRoles);
        adminUser = userRepository.save(adminUser);
        
        UserPrincipal adminPrincipal = new UserPrincipal(adminUser);
        String adminToken = jwtService.generateToken(adminPrincipal);

        mockMvc.perform(post("/api/profile/delete")
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Admin accounts cannot be deleted"));
    }

    @Test
    void testDeleteAccountApi_Unauthenticated_ShouldReturn403() throws Exception {
        mockMvc.perform(post("/api/profile/delete")
                .contentType("application/json"))
                .andExpect(status().isForbidden());
    }
}