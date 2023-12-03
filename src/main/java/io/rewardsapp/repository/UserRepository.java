package io.rewardsapp.repository;

import io.rewardsapp.domain.User;

import java.util.Collection;

public interface UserRepository<T extends User> {
    T create(T date);

    Collection<T> list(int page, int pageSize);

    T get(Long id);

    T update(T data);

    Boolean delete(Long id);
}
