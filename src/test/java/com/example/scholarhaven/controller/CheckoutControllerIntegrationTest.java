package com.example.scholarhaven.controller;

import com.example.scholarhaven.entity.Role;
import com.example.scholarhaven.entity.User;
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

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class CheckoutControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String userToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        Role buyerRole = roleRepository.findByName("BUYER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("BUYER");
                    return roleRepository.save(role);
                });

        User testUser = new User();
        testUser.setName("Test User");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setEnabled(true);
        Set<Role> roles = new HashSet<>();
        roles.add(buyerRole);
        testUser.setRoles(roles);
        testUser = userRepository.save(testUser);
        
        UserPrincipal userPrincipal = new UserPrincipal(testUser);
        userToken = jwtService.generateToken(userPrincipal);
    }

    @Test
    void testCheckoutPage_AuthenticatedUser_ShouldReturnCheckoutPage() throws Exception {
        mockMvc.perform(get("/checkout")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout"));
    }

    @Test
    void testCheckoutPage_Unauthenticated_ShouldReturn403() throws Exception {
        // With stateless JWT, unauthenticated requests return 403
        mockMvc.perform(get("/checkout"))
                .andExpect(status().isForbidden());
    }
}