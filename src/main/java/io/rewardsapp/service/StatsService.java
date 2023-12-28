package io.rewardsapp.service;

import io.rewardsapp.dto.UserStatsDTO;
import jakarta.transaction.Transactional;

public interface StatsService {
    @Transactional
    UserStatsDTO getUserStatsForLastMonth(Long userId);
}
