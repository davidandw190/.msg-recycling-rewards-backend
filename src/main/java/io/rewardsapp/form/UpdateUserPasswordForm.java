package io.rewardsapp.form;

import jakarta.validation.constraints.NotEmpty;

/**
 * Form data for updating user password within the application.
 * Contains fields to specify the current password, new password, and confirmation of the new password.
 */
public record UpdateUserPasswordForm(
        @NotEmpty(message = "Please enter your current password.") String currentPassword,
        @NotEmpty(message = "Please enter a new password.") String newPassword,
        @NotEmpty(message = "Please confirm the new password.") String confirmPassword
) {}