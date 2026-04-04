package com.example.scholarhaven.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestDTOUnitTest {

    @Test
    void testSettersAndGetters() {
        LoginRequest request = new LoginRequest();

        request.setUsername("testuser");
        request.setPassword("password123");

        assertEquals("testuser", request.getUsername());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void testDefaultValues() {
        LoginRequest request = new LoginRequest();

        assertNull(request.getUsername());
        assertNull(request.getPassword());
    }
}