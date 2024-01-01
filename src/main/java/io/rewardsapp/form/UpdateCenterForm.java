package io.rewardsapp.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


/**
 * Form data for updating center information.
 * Contains fields to specify details such as name, contact, location, opening hours, and accepted materials.
 */
public record UpdateCenterForm(
        @NotNull(message = "ID field cannot be null.") Long centerId,
        @NotNull(message = "Please provide a name for the center.") String name,
        @NotNull(message = "Please provide contact information for the center.") String contact,
        @NotNull(message = "Please provide a county for the center.") String county,
        @NotNull(message = "Please provide a city for the center.") String city,
        @NotNull(message = "Please provide an address for the center.") String address,

        String openingHour,
        String closingHour,
        boolean alwaysOpen,

        @NotNull(message = "Please specify the materials accepted by the center.")
        @NotEmpty(message = "Please provide the materials accepted by the center.")

        String[] materials
) {}
