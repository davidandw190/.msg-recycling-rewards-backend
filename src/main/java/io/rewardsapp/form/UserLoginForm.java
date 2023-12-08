package io.rewardsapp.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

/**
 * Represents the form data for user login.
 */
public record UserLoginForm(
        @NotEmpty(message = "Email field cannot be empty")
        @Email(message = "Invalid email. Please enter a valid email address") String email,
        @NotEmpty(message = "Password field cannot be empty. Please enter your password.") String password
) {}