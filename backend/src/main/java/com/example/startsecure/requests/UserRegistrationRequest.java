package com.example.startsecure.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UserRegistrationRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @Email(message = "Invalid email address")
    private String email;

    @NotBlank(message = "Given name is required")
    private String given_name;

    @NotBlank(message = "Family name is required")
    private String family_name;

    @NotBlank(message = "Birthdate is required")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "Birthdate must be in the format yyyy-MM-dd"
    )
    private String birthdate;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGiven_name() {
        return given_name;
    }

    public void setGiven_name(String given_name) {
        this.given_name = given_name;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
}
