package io.rewardsapp.dto;

import lombok.Builder;

@Builder
public record LeaderboardEntryDTO(
        Long userId,
        String firstName,
        String lastName,
        String email,
        String county,
        String city,
        String imageUrl,
        Long totalRewardPoints
) {}
