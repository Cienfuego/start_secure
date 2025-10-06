package com.example.startsecure.service;

import com.example.startsecure.model.User;
import com.example.startsecure.repository.UserRepository;
import com.example.startsecure.requests.UserRegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private CognitoService cognitoService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public User registerUser(UserRegistrationRequest userRegistrationRequest) {
        // Delegate directly to CognitoService which handles all Cognito logic
        User registeredUser = cognitoService.registerUser(userRegistrationRequest);

        if (registeredUser != null) {
            return userRepository.save(registeredUser);
        }
        return null;
    }

    @Override
    public User loginUser(String username, String password) {
        return cognitoService.loginUser(username, password);
    }
}
