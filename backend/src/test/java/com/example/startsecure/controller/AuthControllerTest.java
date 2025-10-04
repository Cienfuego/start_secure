package com.example.startsecure.controller;

import com.example.startsecure.model.User;
import com.example.startsecure.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    private final AuthController controller = new AuthController(userRepository, passwordEncoder);

    @Test
    void register_NewUser_SavesAndReturnsMessage() {
        User user = new User();
        user.setUsername("newuser");
        user.setPassword("pw");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pw")).thenReturn("encodedpw");
        when(userRepository.save(any(User.class))).thenReturn(user);

        String result = controller.register(user);

        assertEquals("User registered", result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ExistingUser_ReturnsAlreadyExists() {
        User existing = new User();
        existing.setUsername("existing");
        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(existing));

        String result = controller.register(existing);

        assertEquals("Username already exists", result);
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginTest_ReturnsMessage() {
        String result = controller.testLogin();
        assertEquals("If you see this, you are authenticated!", result);
    }
}
