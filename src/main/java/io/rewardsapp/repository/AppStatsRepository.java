package io.rewardsapp.repository;

import io.rewardsapp.dto.AppStatsDTO;

public interface AppStatsRepository {
    AppStatsDTO getAppTotalStats();
}
