package io.rewardsapp.repository.implementation;

import io.rewardsapp.dto.UserStatsDTO;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.repository.UserStatsRepository;
import io.rewardsapp.rowmapper.UserStatsRowMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static io.rewardsapp.query.UserStatsQuery.GET_USER_STATS_FOR_LAST_MONTH;

@Repository
@RequiredArgsConstructor
public class UserStatsRepositoryImpl implements UserStatsRepository {
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    @Transactional
    public UserStatsDTO getUserStatsForLastMonth(Long userId) {
        try {
            SqlParameterSource parameters = buildParameters(userId);
            return jdbc.queryForObject(GET_USER_STATS_FOR_LAST_MONTH, parameters, new UserStatsRowMapper());

        } catch (Exception exception) {
            throw new ApiException("Error retrieving user stats for last month.");
        }
    }

    private SqlParameterSource buildParameters(Long userId) {
        LocalDateTime lastMonthStart = LocalDateTime.now().minusMonths(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime lastMonthEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("userId", userId);
        parameterMap.put("startDate", lastMonthStart);
        parameterMap.put("endDate", lastMonthEnd);

        return new MapSqlParameterSource(parameterMap);
    }
}
