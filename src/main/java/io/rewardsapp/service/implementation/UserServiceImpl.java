package io.rewardsapp.service.implementation;

import io.rewardsapp.domain.Role;
import io.rewardsapp.domain.User;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.dto.mapper.UserDTOMapper;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.form.UpdateUserDetailsForm;
import io.rewardsapp.form.UserRegistrationForm;
import io.rewardsapp.repository.JdbcUserRepository;
import io.rewardsapp.repository.RoleRepository;
import io.rewardsapp.service.UserService;
import io.rewardsapp.utils.EmailUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static io.rewardsapp.enums.VerificationType.ACCOUNT;
import static io.rewardsapp.enums.VerificationType.PASSWORD;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    // Repositories
    private final JdbcUserRepository<User> jdbcUserRepository;
    private final RoleRepository<Role> roleRepository;

    private final EmailUtils emailUtils;

    /**
     * Creates a new user and sends an account verification code.
     *
     * @param form The registration form with the new user details.
     * @return The created user as a {@link UserDTO}.
     */
    @Override
    public UserDTO createUser(UserRegistrationForm form) {
        if (!form.password().equals(form.confirmPassword())) {
            throw new ApiException("Password does not matches the confirmation password");
        }

        User newUser = User.builder()
                .firstName(form.firstName())
                .lastName(form.lastName())
                .email(form.email())
                .city(form.city())
                .county(form.county())
                .password(form.password())
                .build();
        UserDTO createdUser = mapToUserDTO(jdbcUserRepository.create(newUser));
        String accountEnableUrl = jdbcUserRepository.createEnableAccountUrl(newUser);

        emailUtils.sendEmail(form.firstName(), form.email(), accountEnableUrl, ACCOUNT);
        return createdUser;
    }

    /**
     * Retrieves a user by email.
     *
     * @param email The email of the user.
     * @return The user as a {@link UserDTO}.
     */
    @Override
    public UserDTO getUser(String email) {
        return mapToUserDTO(jdbcUserRepository.getUserByEmail(email));
    }

    /**
     * Retrieves a user by user ID.
     *
     * @param userId The ID of the user.
     * @return The user as a {@link UserDTO}.
     */
    @Override
    public UserDTO getUser(Long userId) {
        return mapToUserDTO(jdbcUserRepository.get(userId));
    }

    /**
     * Sends an account verification code to the specified user.
     *
     * @param user The user to whom the code will be sent.
     */
    @Override
    public void sendAccountVerificationCode(UserDTO user) {
        jdbcUserRepository.sendAccountVerificationCode(user);
    }

    /**
     * Updates the details of a user.
     *
     * @param updateUserDetailsForm The form containing updated user details.
     * @return The updated user as a {@link UserDTO}.
     */
    @Override
    public UserDTO updateUserDetails(UpdateUserDetailsForm updateUserDetailsForm) {
        return mapToUserDTO(jdbcUserRepository.updateUserDetails(updateUserDetailsForm));
    }

    /**
     * Verifies the account verification code for a user.
     *
     * @param email The email of the user.
     * @param code  The verification code.
     * @return The user as a {@link UserDTO} if verification is successful.
     */
    @Override
    public UserDTO verifyCode(String email, String code) {
        UserDTO loggedInUser = mapToUserDTO(jdbcUserRepository.verifyCode(email, code));
        jdbcUserRepository.updateLastLogin(loggedInUser.id());
        return loggedInUser;
    }

    /**
     * Creates and sends a password reset URL for a user to reset his password externally.
     *
     * @param email The email of the user for whom the password will be reset.
     */

    @Override
    public void resetForgottenPassword(String email) {
        String passwordResetUrl = jdbcUserRepository.resetForgottenPassword(email);
        emailUtils.sendEmail("Recycler", email, passwordResetUrl, PASSWORD);
    }

    /**
     * Verifies the reset password key and retrieves the corresponding user.
     *
     * @param key The reset password key.
     * @return A UserDTO representing the user if the key is valid.
     */
    @Override
    public UserDTO verifyResetPasswordKey(String key) {
        return mapToUserDTO(jdbcUserRepository.verifyResetPasswordKey(key));
    }

    /**
     * Updates the password for a user based on the provided parameters.
     *
     * @param userId            The ID of the user for whom the password will be updated.
     * @param password          The new password.
     * @param confirmPassword   The confirmation of the new password.
     */
    @Override
    public void updatePassword(Long userId, String password, String confirmPassword) {
        jdbcUserRepository.renewPassword(userId, password, confirmPassword);
    }

    /**
     * Updates the password for a user based on the provided parameters.
     *
     * @param userId                The ID of the user for whom the password will be updated.
     * @param currentPassword       The current password for verification.
     * @param newPassword           The new password.
     * @param confirmNewPassword    The confirmation of the new password.
     */
    @Override
    public void updatePassword(Long userId, String currentPassword, String newPassword, String confirmNewPassword) {
        jdbcUserRepository.updatePassword(userId, currentPassword, newPassword, confirmNewPassword);
    }

    /**
     * Verifies the account key and retrieves the corresponding user.
     *
     * @param key The account key for verification.
     * @return A UserDTO representing the user if the key is valid.
     */
    @Override
    public UserDTO verifyAccountKey(String key) {
        return mapToUserDTO(jdbcUserRepository.verifyAccountKey(key));
    }

    /**
     * Toggles multi-factor authentication for a user based on the provided email.
     *
     * @param email The email of the user for whom MFA will be toggled.
     * @return A UserDTO representing the user after the MFA toggle.
     */
    @Override
    public UserDTO toggleMfa(String email) {
        return mapToUserDTO(jdbcUserRepository.toggleMfa(email));
    }

    /**
     * Updates the role of a user.
     *
     * @param userId    The ID of the user for whom the role will be updated.
     * @param roleName  The name of the new role.
     */
    @Override
    public void updateUserRole(Long userId, String roleName) {
        roleRepository.updateUserRole(userId, roleName);
    }

    /**
     * Updates the account settings for a user.
     *
     * @param userId    The ID of the user for whom the account settings will be updated.
     * @param enabled   The new enabled status.
     * @param notLocked The new not locked status.
     */
    @Override
    public void updateAccountSettings(Long userId, Boolean enabled, Boolean notLocked) {
        jdbcUserRepository.updateAccountSettings(userId, enabled, notLocked);
    }

    /**
     * Toggles notifications for a user based on the provided email.
     *
     * @param email The email of the user for whom notifications will be toggled.
     * @return A UserDTO representing the user after the notifications toggle.
     */
    @Override
    public UserDTO toggleNotifications(String email) {
        return mapToUserDTO(jdbcUserRepository.toggleNotifications(email));
    }

    /**
     * Updates the user's image.
     *
     * @param user  The user for whom the image will be updated.
     * @param image The new image file.
     */
    @Override
    public void updateImage(UserDTO user, MultipartFile image) {
        jdbcUserRepository.updateImage(user, image);
    }

    @Override
    public void updateLastLogin(Long userId) {
        jdbcUserRepository.updateLastLogin(userId);
    }

    private UserDTO mapToUserDTO(User user) {
        return UserDTOMapper.fromUser(user, roleRepository.getRoleByUserId(user.getId()));
    }
}
