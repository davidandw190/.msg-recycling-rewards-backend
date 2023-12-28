package io.rewardsapp.repository;

import io.rewardsapp.dto.UserStatsDTO;
import jakarta.transaction.Transactional;

public interface UserStatsRepository {
    @Transactional
    UserStatsDTO getUserStatsForLastMonth(Long userId);
}
