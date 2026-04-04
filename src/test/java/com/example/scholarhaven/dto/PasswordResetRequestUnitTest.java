package com.example.scholarhaven.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordResetRequestUnitTest {

    @Test
    void testSettersAndGetters() {
        PasswordResetRequest request = new PasswordResetRequest();

        request.setEmail("user@example.com");

        assertEquals("user@example.com", request.getEmail());
    }

    @Test
    void testDefaultValues() {
        PasswordResetRequest request = new PasswordResetRequest();

        assertNull(request.getEmail());
    }
}