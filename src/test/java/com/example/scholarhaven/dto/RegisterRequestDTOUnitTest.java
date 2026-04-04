package com.example.scholarhaven.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestDTOUnitTest {

    @Test
    void testSettersAndGetters() {
        RegisterRequest request = new RegisterRequest();

        request.setName("John Doe");
        request.setUsername("johndoe");
        request.setEmail("john@example.com");
        request.setPassword("password123");

        assertEquals("John Doe", request.getName());
        assertEquals("johndoe", request.getUsername());
        assertEquals("john@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void testDefaultValues() {
        RegisterRequest request = new RegisterRequest();

        assertNull(request.getName());
        assertNull(request.getUsername());
        assertNull(request.getEmail());
        assertNull(request.getPassword());
    }
}