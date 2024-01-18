package io.rewardsapp.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LeaderboardEntryDTO {
    private final Long userId;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String county;
    private final String city;
    private final String imageUrl;
    private final Long rewardPoints;
    private final boolean administration;
    private Long rank;
}
