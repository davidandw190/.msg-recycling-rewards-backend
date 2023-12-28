package io.rewardsapp.dto;

import lombok.Builder;

import java.math.BigInteger;

@Builder
public record UserStatsDTO(
      BigInteger paperRecycled,
      BigInteger plasticRecycled,
      BigInteger glassRecycled,
      BigInteger aluminumRecycled,
      BigInteger metalsRecycled,
      BigInteger electronicsRecycled
) {}
