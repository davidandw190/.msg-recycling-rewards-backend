package io.rewardsapp.utils;

public class RewardPointsUtils {

    public static Long computeRewardPointsByUnitsRecycled(Long amountRecycled, Long materialRewardPoints) {
        return amountRecycled * materialRewardPoints;
    }
}
