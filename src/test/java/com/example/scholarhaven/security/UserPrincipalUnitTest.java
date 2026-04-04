package com.example.scholarhaven.security;

import com.example.scholarhaven.entity.Role;
import com.example.scholarhaven.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserPrincipalUnitTest {

    private User testUser;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setEnabled(true);

        Role buyerRole = new Role();
        buyerRole.setId(1L);
        buyerRole.setName("BUYER");

        Role sellerRole = new Role();
        sellerRole.setId(2L);
        sellerRole.setName("SELLER");

        Set<Role> roles = new HashSet<>();
        roles.add(buyerRole);
        roles.add(sellerRole);
        testUser.setRoles(roles);

        userPrincipal = new UserPrincipal(testUser);
    }

    @Test
    void testGetUsername_ShouldReturnUsername() {
        assertEquals("testuser", userPrincipal.getUsername());
    }

    @Test
    void testGetPassword_ShouldReturnEncodedPassword() {
        assertEquals("encodedPassword", userPrincipal.getPassword());
    }

    @Test
    void testGetAuthorities_ShouldReturnGrantedAuthorities() {
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();

        assertEquals(2, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_BUYER")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_SELLER")));
    }

    @Test
    void testIsEnabled_EnabledUser_ShouldReturnTrue() {
        assertTrue(userPrincipal.isEnabled());
    }

    @Test
    void testIsEnabled_DisabledUser_ShouldReturnFalse() {
        testUser.setEnabled(false);
        userPrincipal = new UserPrincipal(testUser);
        assertFalse(userPrincipal.isEnabled());
    }

    @Test
    void testIsAccountNonExpired_ShouldAlwaysReturnTrue() {
        assertTrue(userPrincipal.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked_ShouldAlwaysReturnTrue() {
        assertTrue(userPrincipal.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired_ShouldAlwaysReturnTrue() {
        assertTrue(userPrincipal.isCredentialsNonExpired());
    }

    @Test
    void testGetUser_ShouldReturnOriginalUser() {
        User retrievedUser = userPrincipal.getUser();
        assertSame(testUser, retrievedUser);
    }

    @Test
    void testAuthorities_WithNoRoles_ShouldReturnEmptyList() {
        testUser.setRoles(new HashSet<>());
        userPrincipal = new UserPrincipal(testUser);

        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();
        assertTrue(authorities.isEmpty());
    }
}