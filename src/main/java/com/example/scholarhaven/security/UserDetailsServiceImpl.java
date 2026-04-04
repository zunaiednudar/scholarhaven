package com.example.scholarhaven.security;

import com.example.scholarhaven.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // Spring needs it to find this bean (autowire)
@RequiredArgsConstructor // Needed to inject UserRepository via constructor, mainly for userRepository
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true) // Keeps the Hibernate session open so user.getRoles() can be lazily loaded
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(UserPrincipal::new) // Wraps the User entity in UserPrincipal
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
