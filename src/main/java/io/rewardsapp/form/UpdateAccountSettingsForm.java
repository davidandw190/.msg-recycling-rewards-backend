package io.rewardsapp.form;

import jakarta.validation.constraints.NotNull;

public record UpdateAccountSettingsForm(
        @NotNull(message = "Enabled status cannot be null or empty") Boolean enabled,
        @NotNull(message = "Not Locked status cannot be null or empty") Boolean notLocked
) {}
