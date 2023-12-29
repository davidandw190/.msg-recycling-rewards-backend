package io.rewardsapp.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateCenterForm(
        @NotNull(message = "You must provide a name for the center you create.") String name,
        @NotNull(message = "You must provide contact info for the center you create.") String contact,
        @NotNull(message = "You must provide a county for the center you create.") String county,
        @NotNull(message = "You must provide a city for the center you create.") String city,
        @NotNull(message = "You must provide an address for the center you create.") String address,
        String openingHour,
        String closingHour,
        boolean alwaysOpen,
        @NotNull(message = "You must provide the materials accepted by the center you create.")
        @NotEmpty(message = "You must provide the materials accepted by the center you create.")
        String[] materials
) {
}
