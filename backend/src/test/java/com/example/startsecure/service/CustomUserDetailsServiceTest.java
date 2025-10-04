package com.example.startsecure.service;

import com.example.startsecure.CustomUserDetailsService;
import com.example.startsecure.model.User;
import com.example.startsecure.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final CustomUserDetailsService service = new CustomUserDetailsService(userRepository);

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        User dbUser = new User(1L, "mike", "encodedpw", true, "ROLE_USER");
        when(userRepository.findByUsername("mike")).thenReturn(Optional.of(dbUser));

        UserDetails details = service.loadUserByUsername("mike");

        assertEquals("mike", details.getUsername());
        assertEquals("encodedpw", details.getPassword());
        assertTrue(details.isAccountNonExpired());
        assertTrue(details.isAccountNonLocked());
        assertTrue(details.isCredentialsNonExpired());
        assertTrue(details.isEnabled());
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("ghost"));
    }

    @Test
    void loadUserByUsername_UserNotVerified_IsDisabled() {
        User dbUser = new User(2L, "unverified", "pw", false, "ROLE_USER");
        when(userRepository.findByUsername("unverified")).thenReturn(Optional.of(dbUser));

        UserDetails details = service.loadUserByUsername("unverified");

        assertFalse(details.isEnabled());
    }
}

