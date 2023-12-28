package io.rewardsapp.service.implementation;

import io.rewardsapp.domain.Role;
import io.rewardsapp.domain.User;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.dto.mapper.UserDTOMapper;
import io.rewardsapp.form.UpdateUserDetailsForm;
import io.rewardsapp.repository.RoleRepository;
import io.rewardsapp.service.UserService;
import io.rewardsapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;
    private final RoleRepository<Role> roleRepository;

    @Override
    public UserDTO createUser(User user) {
        UserDTO createdUser = mapToUserDTO(userRepository.create(user));
        userRepository.createAccountVerificationCode(user);
        return createdUser;
    }

    @Override
    public UserDTO getUser(String email) {
        return mapToUserDTO(userRepository.getUserByEmail(email));
    }

    @Override
    public UserDTO getUser(Long userId) {
        return mapToUserDTO(userRepository.get(userId));
    }

    @Override
    public void sendAccountVerificationCode(UserDTO user) {
        userRepository.sendAccountVerificationCode(user);
    }

    @Override
    public UserDTO updateUserDetails(UpdateUserDetailsForm updateUserDetailsForm) {
        return mapToUserDTO(userRepository.updateUserDetails(updateUserDetailsForm));
    }

    @Override
    public UserDTO verifyCode(String email, String code) {
        return mapToUserDTO(userRepository.verifyCode(email, code));
    }

    @Override
    public void resetForgottenPassword(String email) {
        userRepository.resetForgottenPassword(email);
    }

    @Override
    public UserDTO verifyResetPasswordKey(String key) {
        return mapToUserDTO(userRepository.verifyResetPasswordKey(key));
    }

    @Override
    public void updatePassword(Long userId, String password, String confirmPassword) {
        userRepository.renewPassword(userId, password, confirmPassword);
    }

    @Override
    public void updatePassword(Long userId, String currentPassword, String newPassword, String confirmNewPassword) {
        userRepository.updatePassword(userId, currentPassword, newPassword, confirmNewPassword);
    }

    @Override
    public UserDTO verifyAccountKey(String key) {
        return mapToUserDTO(userRepository.verifyAccountKey(key));
    }

    @Override
    public UserDTO toggleMfa(String email) {
        return mapToUserDTO(userRepository.toggleMfa(email));
    }

    @Override
    public void updateUserRole(Long userId, String roleName) {
        roleRepository.updateUserRole(userId, roleName);
    }

    @Override
    public void updateAccountSettings(Long userId, Boolean enabled, Boolean notLocked) {
        userRepository.updateAccountSettings(userId, enabled, notLocked);
    }

    @Override
    public UserDTO toggleNotifications(String email) {
        return mapToUserDTO(userRepository.toggleNotifications(email));
    }

    @Override
    public void updateImage(UserDTO user, MultipartFile image) {
        userRepository.updateImage(user, image);
    }

    private UserDTO mapToUserDTO(User user) {
        return UserDTOMapper.fromUser(user, roleRepository.getRoleByUserId(user.getId()));
    }
}
