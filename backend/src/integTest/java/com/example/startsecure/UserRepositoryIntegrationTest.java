package com.example.startsecure;

import com.example.startsecure.AbstractIntegrationTest;
import com.example.startsecure.model.User;
import com.example.startsecure.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndFindUser() {
        User user = new User();
        user.setUsername("containeruser");
        user.setPassword("encodedpw");
        user.setVerified(true);

        userRepository.save(user);

        assertThat(userRepository.findByUsername("containeruser")).isPresent();
    }
}

