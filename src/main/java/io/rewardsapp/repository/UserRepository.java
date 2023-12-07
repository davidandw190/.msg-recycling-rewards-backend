package io.rewardsapp.repository;

import io.rewardsapp.domain.User;
import io.rewardsapp.dto.UserDTO;

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
}
