package io.rewardsapp.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Form data for resetting a user's password externally.
 * Contains fields necessary for securely resetting a forgotten password.
 */
public record ResetForgottenPasswordForm(
        @NotNull(message = "Please provide a valid user ID") Long userId,
        @NotEmpty(message = "Password field cannot be empty") String password,
        @NotEmpty(message = "Confirm password field cannot be empty") String confirmPassword
) {}