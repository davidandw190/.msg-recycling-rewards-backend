package io.rewardsapp.rowmapper;

import io.rewardsapp.dto.UserStatsDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserStatsRowMapper implements RowMapper<UserStatsDTO> {

    @Override
    public UserStatsDTO mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return UserStatsDTO.builder()
                .paperRecycled(resultSet.getLong("paperRecycled"))
                .plasticRecycled(resultSet.getLong("plasticRecycled"))
                .glassRecycled(resultSet.getLong("glassRecycled"))
                .aluminumRecycled(resultSet.getLong("aluminumRecycled"))
                .metalsRecycled(resultSet.getLong("metalsRecycled"))
                .electronicsRecycled(resultSet.getLong("electronicsRecycled"))
                .build();
    }
}