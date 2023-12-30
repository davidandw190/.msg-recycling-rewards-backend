package io.rewardsapp.form;

import io.rewardsapp.domain.RecyclableMaterial;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record CreateRecyclingActivityForm(
        @NotNull(message = "A valid destination center must be provided.") Long centerId,
        @NotNull(message = "A valid recycler must be provided.") Long userId,
        @NotNull(message = "A valid recycled material must be provided.") Long materialId,
        @NotNull(message = "A valid amount in UNITS must be provided.") Long amount
) {
}