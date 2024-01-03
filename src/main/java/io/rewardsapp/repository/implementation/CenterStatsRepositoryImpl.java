package io.rewardsapp.repository.implementation;

import io.rewardsapp.dto.CenterStatsDTO;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.repository.CenterStatsRepository;
import io.rewardsapp.rowmapper.CenterStatsRowMapper;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

import static io.rewardsapp.query.StatsQuery.GET_CENTER_TOTAL_STATS;

@Repository
@AllArgsConstructor
public class CenterStatsRepositoryImpl implements CenterStatsRepository {
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public CenterStatsDTO getCenterTotalStats(Long centerId) {
        try {
            SqlParameterSource parameters = buildParameters(centerId);
            return jdbc.queryForObject(GET_CENTER_TOTAL_STATS, parameters, new CenterStatsRowMapper());

        } catch (Exception exception) {
            throw new ApiException("Error retrieving center total stats.");
        }
    }

    private SqlParameterSource buildParameters(Long centerId) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("centerId", centerId);
        return new MapSqlParameterSource(parameterMap);
    }
}
