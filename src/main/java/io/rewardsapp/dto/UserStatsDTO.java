package io.rewardsapp.dto;

import lombok.Builder;

@Builder
public record UserStatsDTO(
      Long paperRecycled,
      Long plasticRecycled,
      Long glassRecycled,
      Long aluminumRecycled,
      Long metalsRecycled,
      Long electronicsRecycled
) {}

