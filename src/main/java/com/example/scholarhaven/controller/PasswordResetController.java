package com.example.scholarhaven.controller;

import com.example.scholarhaven.dto.PasswordResetRequest;
import com.example.scholarhaven.dto.ResetPasswordRequest;
import com.example.scholarhaven.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    // Serves the reset password page
    @GetMapping("/reset-password")
    public String resetPasswordPage() {
        return "reset-password";
    }

    // API — sends reset email
    @PostMapping("/api/auth/forgot-password")
    @ResponseBody
    public ResponseEntity<String> forgotPassword(@RequestBody PasswordResetRequest request) {
        passwordResetService.sendResetLink(request.getEmail());
        return ResponseEntity.ok("If this email is registered, a reset link will be sent shortly.");
    }

    // API — resets the password
    @PostMapping("/api/auth/reset-password")
    @ResponseBody
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        boolean success = passwordResetService.resetPassword(
                request.getToken(),
                request.getNewPassword()
        );

        if (!success) {
            return ResponseEntity.badRequest().body("Invalid or expired reset link.");
        }

        return ResponseEntity.ok("Password reset successful. You can now log in.");
    }
}
