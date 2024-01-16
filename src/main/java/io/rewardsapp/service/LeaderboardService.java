package io.rewardsapp.service;

import io.rewardsapp.dto.LeaderboardEntryDTO;

import java.util.List;

public interface LeaderboardService {

     List<LeaderboardEntryDTO> getLeaderboard(String county, int page, int size, String sortBy, String sortOrder);
}
