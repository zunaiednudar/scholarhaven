package com.example.scholarhaven.service;

import com.example.scholarhaven.entity.Book;
import com.example.scholarhaven.entity.Order;
import com.example.scholarhaven.entity.Role;
import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.repository.BookRepository;
import com.example.scholarhaven.repository.OrderRepository;
import com.example.scholarhaven.repository.RoleRepository;
import com.example.scholarhaven.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User testSeller;
    private User testAdmin;
    private Role buyerRole;
    private Role sellerRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setEnabled(true);

        testSeller = new User();
        testSeller.setId(2L);
        testSeller.setName("Test Seller");
        testSeller.setUsername("testseller");
        testSeller.setEmail("seller@example.com");
        testSeller.setPassword("encodedPassword");
        testSeller.setEnabled(true);

        testAdmin = new User();
        testAdmin.setId(3L);
        testAdmin.setName("Test Admin");
        testAdmin.setUsername("testadmin");
        testAdmin.setEmail("admin@example.com");
        testAdmin.setPassword("encodedPassword");
        testAdmin.setEnabled(true);

        buyerRole = new Role();
        buyerRole.setId(1L);
        buyerRole.setName("BUYER");

        sellerRole = new Role();
        sellerRole.setId(2L);
        sellerRole.setName("SELLER");

        adminRole = new Role();
        adminRole.setId(3L);
        adminRole.setName("ADMIN");
    }

    // REGISTRATION TESTS

    @Test
    void testRegisterNewUser_Success() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(roleRepository.findByName("BUYER")).thenReturn(Optional.of(buyerRole));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(10L);
            return u;
        });

        User newUser = new User();
        newUser.setName("New User");
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("password");

        User result = userService.registerNewUser(newUser);

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertTrue(result.getRoles().contains(buyerRole));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterNewUser_DuplicateUsername_ShouldThrowException() {
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        User user = new User();
        user.setUsername("existinguser");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerNewUser(user);
        });

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterNewUser_DuplicateEmail_ShouldThrowException() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        User user = new User();
        user.setUsername("newuser");
        user.setEmail("existing@example.com");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerNewUser(user);
        });

        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void testRegisterNewUser_EmptyName_ShouldThrowException() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

        User user = new User();
        user.setUsername("newuser");
        user.setEmail("new@example.com");
        user.setName(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerNewUser(user);
        });

        assertEquals("Name is required", exception.getMessage());
    }

    // ROLE MANAGEMENT TESTS

    @Test
    void testAddRoleToUser_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName("SELLER")).thenReturn(Optional.of(sellerRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.addRoleToUser("testuser", "SELLER");

        assertTrue(testUser.getRoles().contains(sellerRole));
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testAddRoleToUser_RoleAlreadyExists_ShouldNotSave() {
        testUser.getRoles().add(sellerRole);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName("SELLER")).thenReturn(Optional.of(sellerRole));

        userService.addRoleToUser("testuser", "SELLER");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddRoleToUser_UserNotFound_ShouldThrowException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.addRoleToUser("unknown", "SELLER");
        });

        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    void testRemoveRoleFromUser_Success() {
        testUser.getRoles().add(sellerRole);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName("SELLER")).thenReturn(Optional.of(sellerRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.removeRoleFromUser("testuser", "SELLER");

        assertFalse(testUser.getRoles().contains(sellerRole));
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testMakeUserSeller_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName("SELLER")).thenReturn(Optional.of(sellerRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.makeUserSeller("testuser");

        assertTrue(testUser.getRoles().contains(sellerRole));
    }

    // USER MANAGEMENT TESTS

    @Test
    void testEnableUser_Success() {
        testUser.setEnabled(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.enableUser("testuser");

        assertTrue(testUser.isEnabled());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testDisableUser_Success() {
        testUser.setEnabled(true);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.disableUser("testuser");

        assertFalse(testUser.isEnabled());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testEnableUserById_Success() {
        testUser.setEnabled(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.enableUserById(1L);

        assertTrue(testUser.isEnabled());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testDisableUserById_Success() {
        testUser.setEnabled(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.disableUserById(1L);

        assertFalse(testUser.isEnabled());
        verify(userRepository, times(1)).save(testUser);
    }

    // ROLE CHECK TESTS

    @Test
    void testIsUserSeller_True() {
        testUser.getRoles().add(sellerRole);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        boolean result = userService.isUserSeller("testuser");

        assertTrue(result);
    }

    @Test
    void testIsUserSeller_False() {
        testUser.getRoles().add(buyerRole);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        boolean result = userService.isUserSeller("testuser");

        assertFalse(result);
    }

    @Test
    void testIsUserBuyer_True() {
        testUser.getRoles().add(buyerRole);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        boolean result = userService.isUserBuyer("testuser");

        assertTrue(result);
    }

    @Test
    void testIsUserAdmin_True() {
        testUser.getRoles().add(adminRole);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        boolean result = userService.isUserAdmin("testuser");

        assertTrue(result);
    }

    @Test
    void testGetUserRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(buyerRole);
        roles.add(sellerRole);
        testUser.setRoles(roles);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        Set<Role> result = userService.getUserRoles("testuser");

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> r.getName().equals("BUYER")));
        assertTrue(result.stream().anyMatch(r -> r.getName().equals("SELLER")));
    }

    // STATISTICS TESTS

    @Test
    void testGetTotalUserCount() {
        when(userRepository.count()).thenReturn(5L);

        long result = userService.getTotalUserCount();

        assertEquals(5L, result);
    }

    @Test
    void testGetBuyerCount() {
        when(roleRepository.findByName("BUYER")).thenReturn(Optional.of(buyerRole));
        Set<User> buyerUsers = new HashSet<>();
        buyerUsers.add(testUser);
        buyerRole.setUsers(buyerUsers);

        long result = userService.getBuyerCount();

        assertEquals(1L, result);
    }

    @Test
    void testGetSellerCount() {
        when(roleRepository.findByName("SELLER")).thenReturn(Optional.of(sellerRole));
        Set<User> sellerUsers = new HashSet<>();
        sellerUsers.add(testSeller);
        sellerRole.setUsers(sellerUsers);

        long result = userService.getSellerCount();

        assertEquals(1L, result);
    }

    @Test
    void testGetAdminCount() {
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));
        Set<User> adminUsers = new HashSet<>();
        adminUsers.add(testAdmin);
        adminRole.setUsers(adminUsers);

        long result = userService.getAdminCount();

        assertEquals(1L, result);
    }

    // PASSWORD MANAGEMENT TESTS

    @Test
    void testChangePassword_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPass", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.changePassword("testuser", "oldPass", "newPass");

        assertEquals("newEncodedPassword", testUser.getPassword());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testChangePassword_WrongOldPassword_ShouldThrowException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPass", "encodedPassword")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.changePassword("testuser", "wrongPass", "newPass");
        });

        assertEquals("Old password is incorrect", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testResetPassword_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newPass")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.resetPassword("testuser", "newPass");

        assertEquals("newEncodedPassword", testUser.getPassword());
        verify(userRepository, times(1)).save(testUser);
    }

    // ADMIN HELPER TESTS
@Test
void testGetOrCreateAdmin_ExistingAdmin() {
    when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));
    when(userRepository.existsByUsername("admin")).thenReturn(true);
    
    User existingAdmin = new User();
    existingAdmin.setId(10L);
    existingAdmin.setUsername("admin");
    existingAdmin.setName("Admin User");
    existingAdmin.setEmail("admin@scholarhaven.local");
    existingAdmin.setEnabled(true);
    existingAdmin.getRoles().add(adminRole);
    
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(existingAdmin));

    User result = userService.getOrCreateAdmin("admin", "admin123");

    assertNotNull(result);
    assertEquals("admin", result.getUsername());
}

    @Test
    void testGetOrCreateAdmin_NewAdmin() {
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(passwordEncoder.encode("admin123")).thenReturn("encodedAdminPass");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.getOrCreateAdmin("admin", "admin123");

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertTrue(result.getRoles().contains(adminRole));
    }

    // EMAIL UPDATE TESTS

    @Test
    void testUpdateUserEmail_Success() {
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateUserEmail("testuser", "new@example.com");

        assertEquals("new@example.com", testUser.getEmail());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdateUserEmail_DuplicateEmail_ShouldThrowException() {
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUserEmail("testuser", "existing@example.com");
        });

        assertEquals("Email already in use: existing@example.com", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}