package io.rewardsapp.form;

import jakarta.validation.constraints.NotNull;

/**
 * Represents the form data for updating account settings.
 * Contains fields to control the enabled and locked status of a user account.
 */
public record UpdateAccountSettingsForm(
        @NotNull(message = "Please specify the enabled status") Boolean enabled,
        @NotNull(message = "Please specify the not locked status") Boolean notLocked
) {}