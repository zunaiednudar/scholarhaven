// Wraps User entity and implements UserDetails
// Tells spring security about roles / password / username

package com.example.scholarhaven.security;

import com.example.scholarhaven.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {
    private final User user;

    // Converts Role entities into spring security GrantedAuthority objects
    // Role "ADMIN" becomes "ROLE_ADMIN" which matches hasRole("ADMIN") in SecurityConfig.java
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // Used to decide whether a user is allowed to authenticate and access the system
    // True -> Every user is fully valid and allowed to log in

    @Override
    public boolean isAccountNonExpired() {
        return true; // No expiry
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // No lock
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // No credential expiration
    }

    public User getUser() {
        return user;
    }
}
