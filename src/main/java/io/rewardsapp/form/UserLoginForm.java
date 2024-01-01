package io.rewardsapp.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

/**
 * Form data for user login.
 * Contains fields to specify the user's email and password for authentication.
 */
public record UserLoginForm(
        @NotEmpty(message = "Please enter your email.")
        @Email(message = "Invalid email. Please enter a valid email address") String email,
        @NotEmpty(message = "Please enter your password.") String password
) {}