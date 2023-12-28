package io.rewardsapp.repository.implementation;

import io.rewardsapp.dto.UserStatsDTO;
import io.rewardsapp.query.UserStatsQuery;
import io.rewardsapp.repository.UserStatsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
        LocalDateTime lastMonthStart = LocalDateTime.now().minusMonths(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime lastMonthEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("userId", userId);
        parameters.put("startDate", lastMonthStart);
        parameters.put("endDate", lastMonthEnd);

        return jdbc.queryForObject(
                GET_USER_STATS_FOR_LAST_MONTH,
                parameters,
                (resultSet, i) ->
                        UserStatsDTO.builder()
                                .paperRecycled(resultSet.getLong("paperRecycled"))
                                .plasticRecycled(resultSet.getLong("plasticRecycled"))
                                .glassRecycled(resultSet.getLong("glassRecycled"))
                                .aluminumRecycled(resultSet.getLong("aluminumRecycled"))
                                .metalsRecycled(resultSet.getLong("metalsRecycled"))
                                .electronicsRecycled(resultSet.getLong("electronicsRecycled"))
                                .build()
        );
    }
}
