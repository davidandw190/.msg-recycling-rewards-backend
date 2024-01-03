package io.rewardsapp.rowmapper;

import io.rewardsapp.dto.AppStatsDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigInteger;

public class AppStatsRowMapper implements RowMapper<AppStatsDTO> {

    @Override
    public AppStatsDTO mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return AppStatsDTO.builder()
                .activeRecyclersNumber(new BigInteger(resultSet.getString("activeRecyclersNumber")))
                .monthlyRewardPoints(new BigInteger(resultSet.getString("monthlyRewardPoints")))
                .build();
    }
}
