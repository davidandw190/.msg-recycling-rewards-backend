package io.rewardsapp.dto;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * The UserDTO record represents a data transfer object for user information in the recycling rewards application.
 * It includes information such as user details, reward points, role name, permissions, and creation timestamp.
 */
@Builder
public record UserDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        String county,
        String city,
        String address,
        String phone,
        String bio,
        String imageUrl,

        int rewardPoints,

        boolean notificationsEnabled,

        boolean enabled,
        boolean notLocked,
        boolean usingMfa,

        String roleName,
        String permissions,

        LocalDateTime createdAt
) {}

// TODO: Maybe add a timestamp of the last login in the platform?
