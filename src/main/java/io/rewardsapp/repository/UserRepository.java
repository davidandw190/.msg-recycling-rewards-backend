package io.rewardsapp.repository;

import io.rewardsapp.domain.User;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.form.UpdateUserDetailsForm;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

public interface UserRepository<T extends User> {
    T create(T date);

    Collection<T> list(int page, int pageSize);

    T get(Long userId);

    T update(T data);

    Boolean delete(Long id);

    User getUserByEmail(String email);

    void sendAccountVerificationCode(UserDTO user);

    void createAccountVerificationCode(User user);

    T updateUserDetails(UpdateUserDetailsForm updateUserDetailsForm);

    T verifyCode(String email, String code);

    void resetForgottenPassword(String email);

    T verifyResetPasswordKey(String key);

    void renewPassword(Long userId, String password, String confirmPassword);

    void updatePassword(Long userId, String currentPassword, String newPassword, String confirmNewPassword);

    T verifyAccountKey(String key);

    T toggleMfa(String email);

    void updateAccountSettings(Long userId, Boolean enabled, Boolean notLocked);

    T toggleNotifications(String email);

    void updateImage(UserDTO user, MultipartFile image);
}
