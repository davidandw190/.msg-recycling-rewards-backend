package io.rewardsapp.dto.mapper;

import io.rewardsapp.domain.Role;
import io.rewardsapp.domain.User;
import io.rewardsapp.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserDTOMapper {
    public static UserDTO fromUser(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .address(user.getAddress())
                .phone(user.getPhone())
                .city(user.getCity())
                .bio(user.getBio())
                .imageUrl(user.getImageUrl())
                .enabled(user.isEnabled())
                .notLocked(user.isNotLocked())
                .usingMfa(user.isUsingMfa())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static UserDTO fromUser(User user, Role role) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .address(user.getAddress())
                .phone(user.getPhone())
                .city(user.getCity())
                .bio(user.getBio())
                .imageUrl(user.getImageUrl())
                .enabled(user.isEnabled())
                .notLocked(user.isNotLocked())
                .usingMfa(user.isUsingMfa())
                .roleName(role.getName())
                .permissions(role.getPermission())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static UserDTO fromUser(User user, Role role, int currRewardPoints) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .address(user.getAddress())
                .phone(user.getPhone())
                .city(user.getCity())
                .bio(user.getBio())
                .imageUrl(user.getImageUrl())
                .enabled(user.isEnabled())
                .notLocked(user.isNotLocked())
                .usingMfa(user.isUsingMfa())
                .roleName(role.getName())
                .permissions(role.getPermission())
                .rewardPoints(currRewardPoints)
                .createdAt(user.getCreatedAt())
                .build();
    }




}
