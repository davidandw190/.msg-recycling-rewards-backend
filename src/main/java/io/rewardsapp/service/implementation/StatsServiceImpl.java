package io.rewardsapp.service.implementation;

import io.rewardsapp.dto.AppStatsDTO;
import io.rewardsapp.dto.CenterStatsDTO;
import io.rewardsapp.dto.UserStatsDTO;
import io.rewardsapp.repository.AppStatsRepository;
import io.rewardsapp.repository.CenterStatsRepository;
import io.rewardsapp.repository.UserStatsRepository;
import io.rewardsapp.service.StatsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of the StatsService interface providing methods for retrieving statistics.
 */
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final UserStatsRepository userStatsRepository;
    private final CenterStatsRepository centerStatsRepository;
    private final AppStatsRepository appStatsRepository;

    @Override
    @Transactional
    public UserStatsDTO getUserStatsForLastMonth(Long userId) {
        return userStatsRepository.getUserStatsForLastMonth(userId);
    }

    @Override
    @Transactional
    public CenterStatsDTO getCenterTotalStats(Long centerId) {
        return centerStatsRepository.getCenterTotalStats(centerId);
    }

    @Override
    public AppStatsDTO getAppStats() {
        return appStatsRepository.getAppTotalStats();
    }
}