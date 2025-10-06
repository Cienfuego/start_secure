package com.example.startsecure.service;

import com.example.startsecure.model.User;
import com.example.startsecure.requests.UserRegistrationRequest;

public interface UserService {
    User registerUser(UserRegistrationRequest userRegistrationRequest);
    User loginUser(String username, String password);
}