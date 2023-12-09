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



@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;
    private final RoleRepository<Role> roleRepository;

    @Override
    public UserDTO createNewUser(User user) {
        UserDTO createdUser = mapToUserDTO(userRepository.create(user));
        userRepository.createAccountVerificationCode(user);
        return createdUser;
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return mapToUserDTO(userRepository.getUserByEmail(email));
    }

    @Override
    public UserDTO getUserById(Long userId) {
        return null;
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

    private UserDTO mapToUserDTO(User user) {
        return UserDTOMapper.fromUser(user, roleRepository.getRoleByUserId(user.getId()));
    }
}
