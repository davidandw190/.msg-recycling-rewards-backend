package io.rewardsapp.repository;

import io.rewardsapp.domain.recycling.RewardPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RewardPointsRepository extends JpaRepository<RewardPoints, Long> {

    @Query("SELECT rp.totalPoints FROM RewardPoints rp WHERE rp.userId = :userId")
    Optional<Long> findTotalPointsByUserId(Long userId);

    @Modifying
    @Query("UPDATE RewardPoints rp SET rp.totalPoints = 0 WHERE rp.user.id IN (:userIds)")
    void resetRewardPointsByUserIds(List<Long> userIds);

    @Query("SELECT DISTINCT userId FROM RewardPoints")
    List<Long> findUserIdsOfRecyclers();

    RewardPoints findRewardPointsByUserId(Long userId);

    @Query("SELECT rp FROM RewardPoints rp WHERE rp.userId IN :userIds")
    Collection<RewardPoints> findTotalPointsByUserIds(List<Long> userIds);
}
