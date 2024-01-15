package io.rewardsapp.repository;

import io.rewardsapp.domain.RecyclingCenter;
import io.rewardsapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends PagingAndSortingRepository<User, Long>,
        ListCrudRepository<User, Long>,
        JpaSpecificationExecutor<User> {
}
