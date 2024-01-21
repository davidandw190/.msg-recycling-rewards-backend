package io.rewardsapp.service;

import io.rewardsapp.domain.auth.User;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.form.UpdateUserDetailsForm;
import io.rewardsapp.form.UserRegistrationForm;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;


public interface UserService {

    UserDTO createUser(UserRegistrationForm form);

    UserDTO getUser(String email);

    UserDTO getUser(Long userId);

    void sendAccountVerificationCode(UserDTO user);

    UserDTO updateUserDetails(UpdateUserDetailsForm updateUserDetailsForm);

    UserDTO verifyCode(String email, String code);

    void resetForgottenPassword(String email);

    UserDTO verifyResetPasswordKey(String key);

    void updatePassword(Long userId, String password, String confirmPassword);

    void updatePassword(Long userId, String currentPassword, String newPassword, String confirmNewPassword);

    UserDTO verifyAccountKey(String key);

    UserDTO toggleMfa(String email);

    void updateUserRole(Long userId, String roleName);

    void updateAccountSettings(Long userId, Boolean enabled, Boolean notLocked);

    UserDTO toggleNotifications(String email);

    void updateImage(UserDTO user, MultipartFile image);

    void updateLastLogin(Long userId);

    List<UserDTO> findInactiveUsers(LocalDateTime oneWeekAgo);

    User getJpaManagedUser(Long userId);
}