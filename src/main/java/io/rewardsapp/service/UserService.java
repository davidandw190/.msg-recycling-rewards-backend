package io.rewardsapp.service;

import io.rewardsapp.domain.User;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.form.UpdateUserForm;


public interface UserService {

    UserDTO createNewUser(User user);

    UserDTO getUserByEmail(String email);

    UserDTO getUserById(Long userId);

    void sendAccountVerificationCode(UserDTO user);

    UserDTO updateUserDetails(UpdateUserForm updateUserForm);

    UserDTO verifyCode(String email, String code);
}