package io.rewardsapp.service.implementation;

import io.rewardsapp.dto.UserStatsDTO;
import io.rewardsapp.repository.UserRecyclingActivityRepository;
import io.rewardsapp.repository.UserStatsRepository;
import io.rewardsapp.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of the StatsService interface providing methods for retrieving statistics.
 */
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final UserStatsRepository userStatsRepository;

    @Override
    public UserStatsDTO getUserStatsForLastMonth(Long userId) {
        return userStatsRepository.getUserStatsForLastMonth(userId);
    }
}