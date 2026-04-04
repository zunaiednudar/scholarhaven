package com.example.scholarhaven.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResetPasswordRequestUnitTest {

    @Test
    void testSettersAndGetters() {
        ResetPasswordRequest request = new ResetPasswordRequest();

        request.setToken("abc123-token-xyz");
        request.setNewPassword("newSecurePassword123");

        assertEquals("abc123-token-xyz", request.getToken());
        assertEquals("newSecurePassword123", request.getNewPassword());
    }

    @Test
    void testDefaultValues() {
        ResetPasswordRequest request = new ResetPasswordRequest();

        assertNull(request.getToken());
        assertNull(request.getNewPassword());
    }
}