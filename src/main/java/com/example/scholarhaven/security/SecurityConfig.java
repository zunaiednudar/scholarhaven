package com.example.scholarhaven.security;

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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // PUBLIC STATIC RESOURCES
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico", "/logo-scholarhaven.jpg", "/uploads/**").permitAll()

                        // PUBLIC PAGES
                        .requestMatchers("/", "/index", "/about", "/contact").permitAll()
                        .requestMatchers("/login", "/register", "/forgot-password", "/reset-password").permitAll()

                        // PUBLIC BOOK BROWSING
                        .requestMatchers("/books/search").permitAll()
                        .requestMatchers("/books/category/**").permitAll()
                        .requestMatchers("/books/featured").permitAll()
                        .requestMatchers("/books/new-arrivals").permitAll()
                        .requestMatchers("/books/{id}").permitAll()  // Single book view
                        .requestMatchers("/books").permitAll()  // Main books listing

                        // PUBLIC API ENDPOINTS
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()

                        // SELLER ONLY API
                        .requestMatchers(HttpMethod.POST, "/api/books").hasRole("SELLER")
                        .requestMatchers(HttpMethod.PUT, "/api/books/{id}").hasRole("SELLER")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/{id}").hasRole("SELLER")
                        .requestMatchers("/api/books/with-image").hasRole("SELLER")
                        .requestMatchers("/api/books/seller/me").hasRole("SELLER")
                        .requestMatchers("/api/books/{id}/apply-strategy").hasRole("SELLER")

                        // SELLER ONLY PAGES
                        .requestMatchers("/add-book").hasRole("SELLER")
                        .requestMatchers("/my-books").hasRole("SELLER")
                        .requestMatchers("/books/edit/{id}").hasRole("SELLER")
                        .requestMatchers("/my-books-dashboard").hasRole("SELLER")

                        // ADMIN ONLY
                        .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/books/admin/**").hasRole("ADMIN")  // Admin book operations

                        // PROTECTED PAGES - AUTHENTICATED USERS ONLY
                        // Cart - only authenticated users can view cart
                        .requestMatchers("/cart", "/cart/**").authenticated()

                        // Checkout - only authenticated users can checkout
                        .requestMatchers("/checkout").authenticated()

                        // Orders - only authenticated users can view their orders
                        .requestMatchers("/orders", "/orders/**").authenticated()

                        // Profile - only authenticated users can view/edit profile
                        .requestMatchers("/profile", "/profile/**").authenticated()

                        // Wishlist - only authenticated users can view wishlist
                        .requestMatchers("/wishlist", "/wishlist/**").authenticated()

                        // Seller pages - require authentication (role check for actions)
                        .requestMatchers("/seller", "/seller/**").authenticated()

                        // Become Seller page - require authentication
                        .requestMatchers("/become-seller").authenticated()

                        // PROFILE DELETE ENDPOINTS
                        // POST to /profile/delete requires authentication
                        .requestMatchers(HttpMethod.POST, "/profile/delete").authenticated()
                        // API endpoint for delete
                        .requestMatchers(HttpMethod.POST, "/api/profile/delete").authenticated()

                        // CART API ENDPOINTS
                        // Cart operations require authentication
                        .requestMatchers("/api/cart/**").authenticated()

                        // ORDER API ENDPOINTS
                        // Order operations require authentication
                        .requestMatchers(HttpMethod.POST, "/api/orders").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/orders/**").authenticated()


                        // Any other request requires authentication
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