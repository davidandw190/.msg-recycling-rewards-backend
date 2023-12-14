package io.rewardsapp.service;

import io.rewardsapp.domain.User;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.form.UpdateUserDetailsForm;


public interface UserService {

    UserDTO createNewUser(User user);

    UserDTO getUserByEmail(String email);

    UserDTO getUserById(Long userId);

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
}