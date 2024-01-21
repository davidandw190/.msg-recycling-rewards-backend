package io.rewardsapp.repository;

import io.rewardsapp.domain.auth.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUserRepository extends PagingAndSortingRepository<User, Long>,
        ListCrudRepository<User, Long>,
        JpaSpecificationExecutor<User> {

    Optional<User> findUserById(Long userId);
}
