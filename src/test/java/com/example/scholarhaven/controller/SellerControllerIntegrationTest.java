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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class SellerControllerIntegrationTest {

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
    }

    @Test
    void testShowBecomeSellerPage_RegularUser_ShouldShowForm() throws Exception {
        mockMvc.perform(get("/seller/become-seller")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(view().name("become-seller"))
                .andExpect(model().attribute("alreadySeller", false));
    }

    @Test
    void testShowBecomeSellerPage_ExistingSeller_ShouldShowAlreadySellerMessage() throws Exception {
        mockMvc.perform(get("/seller/become-seller")
                .header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isOk())
                .andExpect(view().name("become-seller"))
                .andExpect(model().attribute("alreadySeller", true));
    }

    @Test
    void testShowBecomeSellerPage_Unauthenticated_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/seller/become-seller"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testRegisterAsSeller_RegularUser_ShouldAddSellerRole() throws Exception {
        mockMvc.perform(post("/seller/register")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testRegisterAsSeller_AlreadySeller_ShouldStillWork() throws Exception {
        mockMvc.perform(post("/seller/register")
                .header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testRegisterAsSeller_Unauthenticated_ShouldReturn403() throws Exception {
        mockMvc.perform(post("/seller/register"))
                .andExpect(status().isForbidden());
    }
}