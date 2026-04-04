```java
package com.example.scholarhaven.controller.api;

import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminApiUnitTest {

    @Mock
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AdminApiController adminApiController;

    private User testUser1;
    private User testUser2;
    private User adminUser;

    @BeforeEach
    void setUp() {
        testUser1 = new User();
        testUser1.setId(1L);
        testUser1.setUsername("john_doe");
        testUser1.setEmail("john@example.com");
        testUser1.setEnabled(true);

        testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setUsername("jane_smith");
        testUser2.setEmail("jane@example.com");
        testUser2.setEnabled(false);

        adminUser = new User();
        adminUser.setId(10L);
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setEnabled(true);
    }

    @Test
    void testGetAllUsers_Success() {
        List<User> mockUsers = Arrays.asList(testUser1, testUser2);
        when(userService.findAllUsers()).thenReturn(mockUsers);

        ResponseEntity<List<Map<String, Object>>> response = adminApiController.getAllUsers(userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        Map<String, Object> firstUser = response.getBody().get(0);
        assertEquals(1L, firstUser.get("id"));
        assertEquals("john_doe", firstUser.get("username"));
        assertEquals("john@example.com", firstUser.get("email"));
        assertEquals(true, firstUser.get("enabled"));

        verify(userService, times(1)).findAllUsers();
    }

    @Test
    void testGetAllUsers_EmptyList() {
        when(userService.findAllUsers()).thenReturn(Arrays.asList());

        ResponseEntity<List<Map<String, Object>>> response = adminApiController.getAllUsers(userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(userService, times(1)).findAllUsers();
    }

    @Test
    void testDeleteUser_Success() {
        Long userId = 2L;
        when(userDetails.getUsername()).thenReturn("admin");
        when(userService.findByUsername("admin")).thenReturn(adminUser);
        doNothing().when(userService).deleteUserById(userId);

        ResponseEntity<?> response = adminApiController.deleteUser(userId, userDetails);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUserById(userId);
    }

    @Test
    void testDeleteUser_SelfDelete_ShouldFail() {
        Long userId = 10L;
        when(userDetails.getUsername()).thenReturn("admin");
        when(userService.findByUsername("admin")).thenReturn(adminUser);

        ResponseEntity<?> response = adminApiController.deleteUser(userId, userDetails);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errorBody = (Map<String, String>) response.getBody();
        assertEquals("You cannot delete your own account", errorBody.get("error"));
        verify(userService, never()).deleteUserById(anyLong());
    }

    @Test
    void testDeleteUser_NotFound() {
        Long userId = 999L;
        when(userDetails.getUsername()).thenReturn("admin");
        when(userService.findByUsername("admin")).thenReturn(adminUser);
        doThrow(new RuntimeException("User not found with id: " + userId))
            .when(userService).deleteUserById(userId);

        ResponseEntity<?> response = adminApiController.deleteUser(userId, userDetails);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errorBody = (Map<String, String>) response.getBody();
        assertTrue(errorBody.get("error").contains("User not found"));
        verify(userService, times(1)).deleteUserById(userId);
    }

    @Test
    void testDisableUser_Success() {
        Long userId = 2L;
        when(userDetails.getUsername()).thenReturn("admin");

        ResponseEntity<?> response = adminApiController.disableUser(userId, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("User disabled successfully", body.get("message"));
        verify(userService, times(1)).disableUserById(userId);
    }

    @Test
    void testDisableUser_NotFound() {
        Long userId = 999L;
        when(userDetails.getUsername()).thenReturn("admin");
        doThrow(new RuntimeException("User not found with id: " + userId))
            .when(userService).disableUserById(userId);

        ResponseEntity<?> response = adminApiController.disableUser(userId, userDetails);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errorBody = (Map<String, String>) response.getBody();
        assertTrue(errorBody.get("error").contains("User not found"));
        verify(userService, times(1)).disableUserById(userId);
    }

    @Test
    void testEnableUser_Success() {
        Long userId = 2L;
        when(userDetails.getUsername()).thenReturn("admin");

        ResponseEntity<?> response = adminApiController.enableUser(userId, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("User enabled successfully", body.get("message"));
        verify(userService, times(1)).enableUserById(userId);
    }

    @Test
    void testEnableUser_NotFound() {
        Long userId = 999L;
        when(userDetails.getUsername()).thenReturn("admin");
        doThrow(new RuntimeException("User not found with id: " + userId))
            .when(userService).enableUserById(userId);

        ResponseEntity<?> response = adminApiController.enableUser(userId, userDetails);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errorBody = (Map<String, String>) response.getBody();
        assertTrue(errorBody.get("error").contains("User not found"));
        verify(userService, times(1)).enableUserById(userId);
    }
}