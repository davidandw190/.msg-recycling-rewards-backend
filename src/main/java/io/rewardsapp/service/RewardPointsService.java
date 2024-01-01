package io.rewardsapp.service;

import io.rewardsapp.domain.RecyclableMaterial;
import io.rewardsapp.domain.User;

public interface RewardPointsService {

    Long getRewardPointsAmount(Long userId);

    void updateUserRewardPoints(User user, Long amountRecycledInUnits, RecyclableMaterial materialRecycled);
}
