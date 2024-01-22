package io.rewardsapp.service;

import io.rewardsapp.domain.recycling.RecyclableMaterial;
import io.rewardsapp.domain.auth.User;
import jakarta.transaction.Transactional;

import java.util.List;

public interface RewardPointsService {

    Long getRewardPointsAmount(Long userId);

    @Transactional
    void updateUserRewardPoints(User user, Long amountRecycledInUnits, RecyclableMaterial materialRecycled);

    @Transactional
    void restoreRecyclersRewardPoints(List<Long> userIds);

    List<Long> getRecyclersIds();

    List<Long> getRewardPointsAmount(List<Long> userIds);
}
