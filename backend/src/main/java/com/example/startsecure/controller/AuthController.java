package com.example.startsecure.controller;


import com.example.startsecure.model.User;
import com.example.startsecure.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return "Username already exists";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerified(true); // TEMP: allow login until we add email verification
        userRepository.save(user);
        return "User registered";
    }

    @GetMapping("/login-test")
    public String testLogin() {
        return "If you see this, you are authenticated!";
    }
}

