package io.rewardsapp.repository;

import io.rewardsapp.domain.RewardPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RewardPointsRepository extends JpaRepository<RewardPoints, Long> {
     Optional<Long> findAmountByUserId(Long userID);
}