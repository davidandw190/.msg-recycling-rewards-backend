package io.rewardsapp.dto;

import lombok.Builder;

import java.math.BigInteger;

@Builder
public record AppStatsDTO(
        BigInteger activeRecyclersNumber,
        BigInteger monthlyRewardPoints
) {}