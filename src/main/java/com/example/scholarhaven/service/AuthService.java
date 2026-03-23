package com.example.scholarhaven.service;

import com.example.scholarhaven.dto.AuthResponse;
import com.example.scholarhaven.dto.LoginRequest;
import com.example.scholarhaven.dto.RegisterRequest;
import com.example.scholarhaven.entity.Role;
import com.example.scholarhaven.entity.User;
import com.example.scholarhaven.repository.RoleRepository;
import com.example.scholarhaven.repository.UserRepository;
import com.example.scholarhaven.security.JwtService;
import com.example.scholarhaven.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if the username or email already exists

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Every new user gets BUYER role by default
        Role role = roleRepository.findByName("BUYER")
                .orElseThrow(() -> new RuntimeException("BUYER role not found in database"));

        // Build and save the new user
        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .build();

        user.addRole(role);
        userRepository.save(user);

        // Generate JWT token for the new user
        UserPrincipal userPrincipal = new UserPrincipal(user);
        String token = jwtService.generateToken(userPrincipal);

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        // AuthenticationManager verifies username + password against database
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Cast the authenticated principal to UserPrincipal
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // Generate and return JWT token
        String token = jwtService.generateToken(userPrincipal);

        return new AuthResponse(token);
    }
}
