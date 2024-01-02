package io.rewardsapp.repository;

import io.rewardsapp.domain.RewardPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RewardPointsRepository extends JpaRepository<RewardPoints, Long> {

    @Query("SELECT rp.totalPoints FROM RewardPoints rp WHERE rp.userId = :userId")
    Optional<Long> findTotalPointsByUserId(Long userId);

    RewardPoints findRewardPointsByUserId(Long userId);
}
