package io.rewardsapp.service;

import io.rewardsapp.domain.recycling.RecyclableMaterial;
import io.rewardsapp.domain.auth.User;

public interface RewardPointsService {

    Long getRewardPointsAmount(Long userId);

    void updateUserRewardPoints(User user, Long amountRecycledInUnits, RecyclableMaterial materialRecycled);
}
