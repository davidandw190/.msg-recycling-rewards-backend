package io.rewardsapp.form;

import jakarta.validation.constraints.NotEmpty;

public record UpdateUserPasswordForm(
        @NotEmpty(message = "Current Password cannot be empty") String currentPassword,
        @NotEmpty(message = "New password cannot be empty") String newPassword,
        @NotEmpty(message = "Confirm password cannot be empty") String confirmPassword
) {}