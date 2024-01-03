package io.rewardsapp.dto;

import lombok.Builder;

@Builder
public record CenterStatsDTO(
        Long recyclersNumber,
        Long activitiesNumber,
        Long paperRecycled,
        Long plasticRecycled,
        Long glassRecycled,
        Long aluminumRecycled,
        Long metalsRecycled,
        Long electronicsRecycled
) {
}
