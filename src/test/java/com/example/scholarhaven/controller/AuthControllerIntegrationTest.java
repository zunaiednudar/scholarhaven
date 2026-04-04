package com.example.scholarhaven.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterUser_Success() throws Exception {
        Map<String, String> request = Map.of(
                "name", "Test User",
                "username", "testuser",
                "email", "test@example.com",
                "password", "password123",
                "confirmPassword", "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registration successful"));
    }

    @Test
    void testRegisterUser_PasswordMismatch() throws Exception {
        Map<String, String> request = Map.of(
                "name", "Test User",
                "username", "testuser2",
                "email", "test2@example.com",
                "password", "password123",
                "confirmPassword", "different"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Passwords do not match"));
    }

    @Test
    void testRegisterUser_DuplicateUsername() throws Exception {
        // First registration
        Map<String, String> request1 = Map.of(
                "name", "User One",
                "username", "duplicateuser",
                "email", "unique1@example.com",
                "password", "password123",
                "confirmPassword", "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        // Second registration with same username
        Map<String, String> request2 = Map.of(
                "name", "User Two",
                "username", "duplicateuser",
                "email", "unique2@example.com",
                "password", "password123",
                "confirmPassword", "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    @Test
    void testRegisterUser_DuplicateEmail() throws Exception {
        // First registration
        Map<String, String> request1 = Map.of(
                "name", "User One",
                "username", "userone",
                "email", "duplicate@example.com",
                "password", "password123",
                "confirmPassword", "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        // Second registration with same email
        Map<String, String> request2 = Map.of(
                "name", "User Two",
                "username", "usertwo",
                "email", "duplicate@example.com",
                "password", "password123",
                "confirmPassword", "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already exists"));  // ← FIXED
    }

    @Test
    void testLogin_Success() throws Exception {
        // First register a user
        Map<String, String> registerRequest = Map.of(
                "name", "Login User",
                "username", "loginuser",
                "email", "login@example.com",
                "password", "password123",
                "confirmPassword", "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Then login
        Map<String, String> loginRequest = Map.of(
                "username", "loginuser",
                "password", "password123"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        Map<String, String> loginRequest = Map.of(
                "username", "nonexistent",
                "password", "wrongpassword"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void testLogin_AdminSuccess() throws Exception {
        Map<String, String> loginRequest = Map.of(
                "username", "admin",
                "password", "admin123"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testForgotPassword_WithValidEmail() throws Exception {
        // First register a user
        Map<String, String> registerRequest = Map.of(
                "name", "Reset User",
                "username", "resetuser",
                "email", "reset@example.com",
                "password", "password123",
                "confirmPassword", "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Request password reset
        Map<String, String> forgotRequest = Map.of("email", "reset@example.com");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("If this email is registered, a reset link will be sent shortly."));
    }

    @Test
    void testForgotPassword_WithInvalidEmail() throws Exception {
        Map<String, String> forgotRequest = Map.of("email", "nonexistent@example.com");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("If this email is registered, a reset link will be sent shortly."));
    }
}