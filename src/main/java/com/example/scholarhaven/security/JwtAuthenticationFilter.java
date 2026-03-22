package com.example.scholarhaven.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

@Component // Required for Spring to find and autowire this
@RequiredArgsConstructor
// Automatically generates a constructor for the class (Includes all final fields, all fields marked with @NonNull)
public class JwtAuthenticationFilter extends OncePerRequestFilter { // OncePerRequestFilter -> Filter is executed only once per HTTP request
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String jwt = null;

        // Try to get token from cookie first
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jwt")) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        // If not in cookie, try Authorization header
        if (jwt == null) {
            final String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // "Bearer " -> 7 letters
                // Actual JWT token comes after the space
                jwt = authHeader.substring(7);
            }
        }

        // If no token found in either place, skip
        if (jwt == null) {
            filterChain.doFilter(request, response); // Pass this request and response to the next filter / controller (if this is the last filter)
            return;
        }

        // Validate and authenticate
        final String username = jwtService.extractUsername(jwt);

        // Only authenticate if not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Spring security object that represents an authenticated user
            // Constructor parameters -> principal, credentials, authorities
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // WebAuthenticationDetailsSource().buildDetails(request) -> Creates additional details about the request
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Spring security's thread-local storage for security information
                // .getContext() -> Gets the current security context for this request / thread
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
