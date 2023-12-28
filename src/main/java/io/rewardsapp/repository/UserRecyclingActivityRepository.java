package io.rewardsapp.repository;

import io.rewardsapp.domain.RecyclingCenter;
import io.rewardsapp.domain.User;
import io.rewardsapp.domain.UserRecyclingActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for accessing user recycling activities data.
 */
@Repository
public interface UserRecyclingActivityRepository extends JpaRepository<UserRecyclingActivity, Long> {
    List<UserRecyclingActivity> findAllByUserAndRecyclingCenter(User user, RecyclingCenter recyclingCenter);

}