package io.rewardsapp.dto;

import lombok.Builder;

/**
 * Data Transfer Object (DTO) representing recycling statistics for a user.
 * It encapsulates the recycling quantities for different materials.
 */
@Builder
public record UserStatsDTO(
      Long paperRecycled,
      Long plasticRecycled,
      Long glassRecycled,
      Long aluminumRecycled,
      Long metalsRecycled,
      Long electronicsRecycled
) {}

