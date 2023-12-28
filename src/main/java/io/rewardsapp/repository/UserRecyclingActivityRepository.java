package io.rewardsapp.repository;

import io.rewardsapp.domain.UserRecyclingActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository for accessing user recycling activities data.
 */
@Repository
public interface UserRecyclingActivityRepository extends JpaRepository<UserRecyclingActivity, Long>, JpaSpecificationExecutor<UserRecyclingActivity> {

}