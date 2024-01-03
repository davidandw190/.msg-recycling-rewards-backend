package io.rewardsapp.repository;

import io.rewardsapp.dto.CenterStatsDTO;

public interface CenterStatsRepository {
    CenterStatsDTO getCenterTotalStats(Long centerId);
}
