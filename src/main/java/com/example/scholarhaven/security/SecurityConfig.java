package com.example.scholarhaven.security;

import com.example.scholarhaven.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public static resources + uploads and logo
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico", "/logo-scholarhaven.jpg", "/uploads/**").permitAll()

                        // Public pages
                        .requestMatchers("/", "/index", "/about", "/contact").permitAll()
                        .requestMatchers("/login", "/register", "/forgot-password", "/reset-password").permitAll()
                            
                        // Public book browsing
                        .requestMatchers("/books", "/books/**", "/books/search").permitAll()
                        .requestMatchers("/books/category/**", "/books/featured", "/books/new-arrivals").permitAll()

                        // Public API endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()

                        // Protected pages - any authenticated user
                        .requestMatchers("/profile/**", "/cart/**", "/wishlist/**").authenticated()
                        .requestMatchers("/seller/**").authenticated()

                        // Seller only pages
                        .requestMatchers("/add-book", "/my-books", "/books/edit/**").hasRole("SELLER")
                        .requestMatchers("/my-books-dashboard").hasRole("SELLER")

                        // Seller only API
                        .requestMatchers(HttpMethod.POST, "/api/books").hasRole("SELLER")
                        .requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("SELLER")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("SELLER")

                        // Admin only
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}