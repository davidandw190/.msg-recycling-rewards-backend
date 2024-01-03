package io.rewardsapp.service;

import io.rewardsapp.dto.AppStatsDTO;
import io.rewardsapp.dto.CenterStatsDTO;
import io.rewardsapp.dto.UserStatsDTO;
import jakarta.transaction.Transactional;

public interface StatsService {
    UserStatsDTO getUserStatsForLastMonth(Long userId);

    CenterStatsDTO getCenterTotalStats(Long centerId);

    AppStatsDTO getAppStats();

}
