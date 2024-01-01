package io.rewardsapp.form;

import jakarta.validation.constraints.NotNull;


/**
 * Form used for creating a new recycling activity.
 * Contains fields for essential information about the recycling activity.
 */
public record CreateRecyclingActivityForm(
        @NotNull(message = "Please provide a valid destination recycling center.") Long centerId,
        @NotNull(message = "Please provide a valid recycler.") Long userId,
        @NotNull(message = "Please provide a valid recycled material.") Long materialId,
        @NotNull(message = "Please provide a valid amount in UNITS for recycling.") Long amount,

        String recycledMaterialType
) {}
