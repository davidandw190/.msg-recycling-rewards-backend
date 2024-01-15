package io.rewardsapp.repository;

import io.rewardsapp.domain.User;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.form.UpdateUserDetailsForm;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface JdbcUserRepository<T extends User> {
    T create(T date);

    Collection<T> list(int page, int pageSize);

    T get(Long userId);

    T update(T data);

    Boolean delete(Long id);

    User getUserByEmail(String email);

    void sendAccountVerificationCode(UserDTO user);

    String createEnableAccountUrl(User user);

    T updateUserDetails(UpdateUserDetailsForm updateUserDetailsForm);

    T verifyCode(String email, String code);

    String resetForgottenPassword(String email);

    T verifyResetPasswordKey(String key);

    void renewPassword(Long userId, String password, String confirmPassword);

    void updatePassword(Long userId, String currentPassword, String newPassword, String confirmNewPassword);

    T verifyAccountKey(String key);

    T toggleMfa(String email);

    void updateAccountSettings(Long userId, Boolean enabled, Boolean notLocked);

    T toggleNotifications(String email);

    void updateImage(UserDTO user, MultipartFile image);

    void updateLastLogin(Long userId);

    List<User> getInactiveUsers(LocalDateTime oneWeekAgo);
}
