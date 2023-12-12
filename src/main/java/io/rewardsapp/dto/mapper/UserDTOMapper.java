package io.rewardsapp.dto.mapper;

import io.rewardsapp.domain.Role;
import io.rewardsapp.domain.User;
import io.rewardsapp.dto.UserDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * The UserDTOMapper class provides static methods for mapping between User, Role, and UserDTO objects.
 * It facilitates the conversion of entities to data transfer objects and vice versa.
 */
@Component
public class UserDTOMapper {
    /**
     * Maps a User entity to a UserDTO object.
     *
     * @param user The User entity to be mapped.
     * @return A UserDTO object representing the mapped user information.
     */
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

    /**
     * Maps a User entity and a Role entity to a UserDTO object.
     *
     * @param user The User entity to be mapped.
     * @param role The Role entity to be mapped.
     * @return A UserDTO object representing the mapped user and role information.
     */
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
                .notificationsEnabled(user.isNotificationsEnabled())
                .enabled(user.isEnabled())
                .notLocked(user.isNotLocked())
                .usingMfa(user.isUsingMfa())
                .roleName(role.getName())
                .permissions(role.getPermission())
                .createdAt(user.getCreatedAt())
                .build();
    }

    /**
     * Maps a User entity, a Role entity, and reward points to a UserDTO object.
     *
     * @param user              The User entity to be mapped.
     * @param role              The Role entity to be mapped.
     * @param currRewardPoints  The current reward points associated with the user.
     * @return A UserDTO object representing the mapped user, role, and reward points information.
     */
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
                .notificationsEnabled(user.isNotificationsEnabled())
                .enabled(user.isEnabled())
                .notLocked(user.isNotLocked())
                .usingMfa(user.isUsingMfa())
                .roleName(role.getName())
                .permissions(role.getPermission())
                .rewardPoints(currRewardPoints)
                .createdAt(user.getCreatedAt())
                .build();
    }

    /**
     * Converts a UserDTO object to a User entity.
     *
     * @param userDTO The UserDTO object to be converted.
     * @return A User entity representing the converted user information.
     */
    public static User toUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        return user;
    }

}
