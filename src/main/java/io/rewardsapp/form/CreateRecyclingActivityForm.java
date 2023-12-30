package io.rewardsapp.form;

import jakarta.validation.constraints.NotNull;

public record CreateRecyclingActivityForm(
        @NotNull(message = "A valid destination center must be provided.") Long centerId,
        @NotNull(message = "A valid recycler must be provided.") Long userId,
        @NotNull(message = "A valid recycled material must be provided.") Long materialId,
        @NotNull(message = "A valid amount in UNITS must be provided.") Long amount,
        String recycledMaterialType
) {
}
