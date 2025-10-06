package com.example.startsecure.controller;

import com.example.startsecure.model.User;
import com.example.startsecure.requests.UserLoginRequest;
import com.example.startsecure.requests.UserRegistrationRequest;
import com.example.startsecure.service.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {
        System.out.println("Registering user: " + userRegistrationRequest.getUsername());

        User registeredUser = userService.registerUser(userRegistrationRequest);
        if (registeredUser != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<User> loginUser(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        System.out.println("Attempting login for: " + userLoginRequest.getUsername());

        User loggedInUser = userService.loginUser(
                userLoginRequest.getUsername(),
                userLoginRequest.getPassword()
        );

        if (loggedInUser != null) {
            return ResponseEntity.ok(loggedInUser);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
