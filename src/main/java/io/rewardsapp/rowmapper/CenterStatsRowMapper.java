package io.rewardsapp.rowmapper;

import io.rewardsapp.dto.CenterStatsDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CenterStatsRowMapper implements RowMapper<CenterStatsDTO> {

    @Override
    public CenterStatsDTO mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return CenterStatsDTO.builder()
                .recyclersNumber(resultSet.getLong("recyclersNumber"))
                .activitiesNumber(resultSet.getLong("activitiesNumber"))
                .paperRecycled(resultSet.getLong("paperRecycled"))
                .plasticRecycled(resultSet.getLong("plasticRecycled"))
                .glassRecycled(resultSet.getLong("glassRecycled"))
                .aluminumRecycled(resultSet.getLong("aluminumRecycled"))
                .metalsRecycled(resultSet.getLong("metalsRecycled"))
                .electronicsRecycled(resultSet.getLong("electronicsRecycled"))
                .build();
    }
}