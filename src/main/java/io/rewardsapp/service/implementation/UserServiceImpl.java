package io.rewardsapp.service.implementation;

import io.rewardsapp.domain.Role;
import io.rewardsapp.domain.User;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.dto.mapper.UserDTOMapper;
import io.rewardsapp.form.UpdateUserForm;
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
    public UserDTO updateUserDetails(UpdateUserForm updateUserForm) {
        return mapToUserDTO(userRepository.updateUserDetails(updateUserForm));
    }

    @Override
    public UserDTO verifyCode(String email, String code) {
        return mapToUserDTO(userRepository.verifyCode(email, code));
    }

    @Override
    public void resetForgottenPassword(String email) {
        userRepository.resetForgottenPassword(email);
    }

    private UserDTO mapToUserDTO(User user) {
        return UserDTOMapper.fromUser(user, roleRepository.getRoleByUserId(user.getId()));
    }
}
