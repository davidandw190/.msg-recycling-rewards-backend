package io.rewardsapp.service.implementation;

import io.rewardsapp.repository.RewardPointsRepository;
import io.rewardsapp.service.RewardPointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RewardPointsServiceImpl implements RewardPointsService {
    private final RewardPointsRepository rewardPointsRepository;

    @Override
    public Long getRewardPointsAmount(Long userId) {
        return rewardPointsRepository.findAmountByUserId(userId).orElse(0L);
    }
}
