package com.example.scholarhaven.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthResponseUnitTest {

    @Test
    void testConstructorAndGetters() {
        AuthResponse response = new AuthResponse("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9", "testuser");

        assertEquals("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9", response.getToken());
        assertEquals("testuser", response.getUsername());
    }

    @Test
    void testNullValues() {
        AuthResponse response = new AuthResponse(null, null);

        assertNull(response.getToken());
        assertNull(response.getUsername());
    }

    @Test
    void testEmptyStrings() {
        AuthResponse response = new AuthResponse("", "");

        assertEquals("", response.getToken());
        assertEquals("", response.getUsername());
    }
}