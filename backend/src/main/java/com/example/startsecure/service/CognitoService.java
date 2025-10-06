package com.example.startsecure.service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;
import com.example.startsecure.model.User;
import com.example.startsecure.repository.UserRepository;
import com.example.startsecure.requests.UserRegistrationRequest;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class CognitoService {

    @Value("${spring.security.oauth2.client.registration.cognito.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.cognito.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.cognito.scope}")
    private String scope;

    @Value("${spring.security.oauth2.client.provider.cognito.issuer-uri}")
    private String issuerUri;

    @Value("${aws.region}")
    private String region;

    @Autowired
    private AWSCognitoIdentityProvider cognitoIdentityProvider;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        this.cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.standard()
                .withRegion(region)
                .build();
    }

    public User registerUser(UserRegistrationRequest request) {
        try {
            SignUpRequest signUpRequest = new SignUpRequest()
                    .withClientId(clientId)
                    .withUsername(request.getUsername())
                    .withPassword(request.getPassword())
                    .withUserAttributes(
                            new AttributeType().withName("email").withValue(request.getEmail()),
                            new AttributeType().withName("given_name").withValue(request.getGiven_name()),
                            new AttributeType().withName("family_name").withValue(request.getFamily_name()),
                            new AttributeType().withName("birthdate").withValue(request.getBirthdate())
                    );

            // Include SECRET_HASH if applicable
            if (clientSecret != null && !clientSecret.isEmpty()) {
                signUpRequest.setSecretHash(calculateSecretHash(request.getUsername()));
            }

            SignUpResult signUpResponse = cognitoIdentityProvider.signUp(signUpRequest);
            System.out.println("Cognito signup response: " + signUpResponse);

            User registeredUser = new User();
            registeredUser.setUsername(request.getUsername());
            registeredUser.setEmail(request.getEmail());
            registeredUser.setPassword(request.getPassword());

            return userRepository.save(registeredUser);

        } catch (InvalidParameterException e) {
            throw new RuntimeException("Cognito rejected user attributes: " + e.getErrorMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("User registration failed: " + e.getMessage(), e);
        }
    }

    public User loginUser(String username, String password) {
        Map<String, String> authParams = new HashMap<>();
        authParams.put("USERNAME", username);
        authParams.put("PASSWORD", password);

        if (clientSecret != null && !clientSecret.isEmpty()) {
            authParams.put("SECRET_HASH", calculateSecretHash(username));
        }

        InitiateAuthRequest authRequest = new InitiateAuthRequest()
                .withAuthFlow("USER_PASSWORD_AUTH")
                .withClientId(clientId)
                .withAuthParameters(authParams);

        try {
            InitiateAuthResult authResult = cognitoIdentityProvider.initiateAuth(authRequest);
            AuthenticationResultType authResponse = authResult.getAuthenticationResult();

            String accessToken = authResponse.getAccessToken();
            String idToken = authResponse.getIdToken();
            String refreshToken = authResponse.getRefreshToken();

            User loggedInUser = new User();
            loggedInUser.setUsername(username);
            loggedInUser.setAccessToken(accessToken);
            return loggedInUser;

        } catch (Exception e) {
            throw new RuntimeException("User login failed: " + e.getMessage(), e);
        }
    }

    private String calculateSecretHash(String username) {
        if (clientSecret == null || clientSecret.isEmpty()) {
            return null; // public client
        }

        try {
            String message = username + clientId;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(clientSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error computing secret hash", e);
        }
    }
}
