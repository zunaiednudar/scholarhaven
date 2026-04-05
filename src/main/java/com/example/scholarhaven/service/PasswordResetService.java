package com.example.scholarhaven.service;

import com.example.scholarhaven.entity.PasswordResetToken;
import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.repository.PasswordResetTokenRepository;
import com.example.scholarhaven.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Transactional
    public void sendResetLink(String email) {
        // Silently do nothing if email not found — prevents email enumeration attacks
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return;

        // Delete any existing token for this user
        tokenRepository.deleteByUser_Id(user.getId());

        // Generate a secure random token
        String token = UUID.randomUUID().toString();

        // Save token with 1 hour expiry
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();

        tokenRepository.save(resetToken);

        // Send the email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("ScholarHaven — Password Reset Request");
        message.setText(
                "Hello " + user.getName() + ",\n\n" +
                        "You requested a password reset for your ScholarHaven account.\n\n" +
                        "Click the link below to reset your password:\n" +
                        baseUrl + "/reset-password?token=" + token + "\n\n" +
                        "This link expires in 1 hour.\n\n" +
                        "If you did not request this, please ignore this email.\n\n" +
                        "ScholarHaven Team"
        );

        mailSender.send(message);
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElse(null);

        // Invalid, expired, or already used token
        if (resetToken == null || resetToken.isExpired() || resetToken.isUsed()) {
            return false;
        }

        // Update password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        return true;
    }
}