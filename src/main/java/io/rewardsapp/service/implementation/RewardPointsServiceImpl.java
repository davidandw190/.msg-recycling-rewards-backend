package io.rewardsapp.service.implementation;

import io.rewardsapp.domain.recycling.RecyclableMaterial;
import io.rewardsapp.domain.recycling.RewardPoints;
import io.rewardsapp.domain.auth.User;
import io.rewardsapp.repository.RewardPointsRepository;
import io.rewardsapp.service.RewardPointsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.rewardsapp.utils.RewardPointsUtils.computeRewardPointsByUnitsRecycled;

@Service
@RequiredArgsConstructor
public class RewardPointsServiceImpl implements RewardPointsService {
    private final RewardPointsRepository rewardPointsRepository;

    @Override
    public Long getRewardPointsAmount(Long userId) {
        return rewardPointsRepository.findTotalPointsByUserId(userId).orElse(0L);
    }

    /**
     * Updates the user's reward points based on the recycling activity.
     *
     * @param user                    The user for whom reward points are updated.
     * @param amountRecycledInUnits  The amount recycled in units.
     * @param materialRecycled        The recyclable material being recycled.
     */
    @Override
    @Transactional
    public void updateUserRewardPoints(User user, Long amountRecycledInUnits, RecyclableMaterial materialRecycled) {
        Long additionalRewardPoints = computeRewardPointsByUnitsRecycled(amountRecycledInUnits, materialRecycled.getRewardPoints());

        // Check if the user has existing reward points
        RewardPoints existingRewardPoints = rewardPointsRepository.findRewardPointsByUserId(user.getId());

        if (existingRewardPoints == null) {
            // If the user doesn't have reward points entry, create a new one
            RewardPoints newRewardPoints = RewardPoints.builder()
                    .user(user)
                    .totalPoints(additionalRewardPoints)
                    .lastUpdated(LocalDateTime.now())
                    .build();

            rewardPointsRepository.save(newRewardPoints);

        } else {
            // If the user has existing reward points entry, update it
            existingRewardPoints.setTotalPoints(existingRewardPoints.getTotalPoints() + additionalRewardPoints);
            existingRewardPoints.setLastUpdated(LocalDateTime.now());

            rewardPointsRepository.save(existingRewardPoints);
        }
    }

    @Override
    @Transactional
    public void restoreRecyclersRewardPoints(List<Long> userIds) {
        rewardPointsRepository.resetRewardPointsByUserIds(userIds);
    }

    @Override
    public List<Long> getRecyclersIds() {
        return rewardPointsRepository.findUserIdsOfRecyclers();
    }

    @Override
    @Transactional
    public List<Long> getRewardPointsAmount(List<Long> userIds) {
        Map<Long, Long> rewardPointsMap = rewardPointsRepository.findTotalPointsByUserIds(userIds)
                .stream()
                .collect(Collectors.toMap(
                        RewardPoints::getUserId,
                        RewardPoints::getTotalPoints,
                        (existing, replacement) -> existing));

        return userIds.stream()
                .map(userId -> rewardPointsMap.getOrDefault(userId, 0L))
                .collect(Collectors.toList());
    }

}
