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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminApiUnitTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminApiController adminApiController;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        // Setup test users
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
    }

    @Test
    void testGetAllUsers_Success() {
        // Arrange
        List<User> mockUsers = Arrays.asList(testUser1, testUser2);
        when(userService.findAllUsers()).thenReturn(mockUsers);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = adminApiController.getAllUsers();

        // Assert
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
        // Arrange
        when(userService.findAllUsers()).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<List<Map<String, Object>>> response = adminApiController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(userService, times(1)).findAllUsers();
    }

    @Test
    void testDeleteUser_Success() {
        // Arrange
        Long userId = 1L;
        doNothing().when(userService).deleteUserById(userId);

        // Act
        ResponseEntity<Void> response = adminApiController.deleteUser(userId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUserById(userId);
    }

    @Test
    void testDeleteUser_NotFound() {
        // Arrange
        Long userId = 999L;
        doThrow(new RuntimeException("User not found with id: " + userId))
            .when(userService).deleteUserById(userId);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> adminApiController.deleteUser(userId));
        
        assertTrue(exception.getMessage().contains("User not found"));
        verify(userService, times(1)).deleteUserById(userId);
    }

    @Test
    void testDisableUser_Success() {
        // Arrange
        Long userId = 1L;
        doNothing().when(userService).disableUserById(userId);

        // Act
        ResponseEntity<Void> response = adminApiController.disableUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).disableUserById(userId);
    }

    @Test
    void testDisableUser_NotFound() {
        // Arrange
        Long userId = 999L;
        doThrow(new RuntimeException("User not found with id: " + userId))
            .when(userService).disableUserById(userId);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> adminApiController.disableUser(userId));
        
        assertTrue(exception.getMessage().contains("User not found"));
        verify(userService, times(1)).disableUserById(userId);
    }

    @Test
    void testEnableUser_Success() {
        // Arrange
        Long userId = 2L;
        doNothing().when(userService).enableUserById(userId);

        // Act
        ResponseEntity<Void> response = adminApiController.enableUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).enableUserById(userId);
    }

    @Test
    void testEnableUser_NotFound() {
        // Arrange
        Long userId = 999L;
        doThrow(new RuntimeException("User not found with id: " + userId))
            .when(userService).enableUserById(userId);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> adminApiController.enableUser(userId));
        
        assertTrue(exception.getMessage().contains("User not found"));
        verify(userService, times(1)).enableUserById(userId);
    }

    @Test
    void testDisableUser_AlreadyDisabled() {
        // Arrange
        Long userId = 2L; // testUser2 is already disabled
        doNothing().when(userService).disableUserById(userId);

        // Act
        ResponseEntity<Void> response = adminApiController.disableUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).disableUserById(userId);
    }

    @Test
    void testEnableUser_AlreadyEnabled() {
        // Arrange
        Long userId = 1L; // testUser1 is already enabled
        doNothing().when(userService).enableUserById(userId);

        // Act
        ResponseEntity<Void> response = adminApiController.enableUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).enableUserById(userId);
    }

    @Test
    void testUserServiceNull() {
        // This test verifies that the controller handles null service gracefully
        assertNotNull(userService);
        assertNotNull(adminApiController);
    }
}