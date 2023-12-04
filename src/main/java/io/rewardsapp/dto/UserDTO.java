package io.rewardsapp.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        String city,
        String address,
        String phone,
        String bio,
        String imageUrl,

        int rewardPoints,

        boolean enabled,
        boolean notLocked,
        boolean usingMfa,

        String roleName,
        String permissions,

        LocalDateTime createdAt
) {}

// TODO: Maybe add a timestamp of the last login in the platform?
