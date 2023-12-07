package io.rewardsapp.service;

import io.rewardsapp.domain.User;
import io.rewardsapp.dto.UserDTO;


public interface UserService {

    UserDTO createNewUser(User user);

    UserDTO getUserByEmail(String email);

    UserDTO getUserById(Long userId);

    void sendAccountVerificationCode(UserDTO user);
}