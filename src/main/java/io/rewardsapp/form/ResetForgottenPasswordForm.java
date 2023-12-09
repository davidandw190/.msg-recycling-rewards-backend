package io.rewardsapp.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Represents the form data for resetting a users password externally.
 */
public record ResetForgottenPasswordForm(
        @NotNull(message = "ID cannot be null or empty") Long userId,
        @NotEmpty(message = "Password field cannot be empty") String password,
        @NotEmpty(message = "Confirm password field cannot be empty") String confirmPassword
) {}
