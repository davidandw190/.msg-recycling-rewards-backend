package io.rewardsapp.service;

import io.rewardsapp.dto.LeaderboardEntryDTO;
import org.springframework.data.domain.Page;

public interface LeaderboardService {

     Page<LeaderboardEntryDTO> getLeaderboard(String county, int page, int size, String sortBy, String sortOrder);
}
