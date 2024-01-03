package io.rewardsapp.repository.implementation;

import io.rewardsapp.dto.AppStatsDTO;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.repository.AppStatsRepository;
import io.rewardsapp.rowmapper.AppStatsRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import static io.rewardsapp.query.StatsQuery.GET_APP_TOTAL_STATS;

@Repository
@RequiredArgsConstructor
public class AppStatsRepositoryImpl implements AppStatsRepository {
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public AppStatsDTO getAppTotalStats() {
        try {
            return jdbc.queryForObject(GET_APP_TOTAL_STATS, new MapSqlParameterSource(), new AppStatsRowMapper());
        } catch (Exception exception) {
            throw new ApiException("Error retrieving app total stats.");
        }
    }

}
