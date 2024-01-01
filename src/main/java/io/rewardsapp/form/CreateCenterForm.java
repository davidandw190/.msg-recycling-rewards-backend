package io.rewardsapp.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Form used to create a new recycling center.
 */
public record CreateCenterForm(
        @NotNull(message = "Please provide a name for the new center.") String name,
        @NotNull(message = "Please provide contact information for the new center.") String contact,
        @NotNull(message = "Please specify the county where the center is located.") String county,
        @NotNull(message = "Please specify the city where the center is located.") String city,
        @NotNull(message = "Please provide the address for the new center.") String address,

        String openingHour,
        String closingHour,
        boolean alwaysOpen,

        @NotNull(message = "Please specify the materials accepted by the new center.")
        @NotEmpty(message = "Please provide at least one material accepted by the new center.")

        String[] materials
) {}
