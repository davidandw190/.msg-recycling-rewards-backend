package io.rewardsapp.query;

public class UserStatsQuery {

    public static final String GET_USER_STATS_FOR_LAST_MONTH =
            "SELECT " +
                    "COALESCE(SUM(CASE WHEN m.name = 'PAPER' THEN ura.amount END), 0) AS paperRecycled, " +
                    "COALESCE(SUM(CASE WHEN m.name = 'PLASTIC' THEN ura.amount END), 0) AS plasticRecycled, " +
                    "COALESCE(SUM(CASE WHEN m.name = 'GLASS' THEN ura.amount END), 0) AS glassRecycled, " +
                    "COALESCE(SUM(CASE WHEN m.name = 'ALUMINUM' THEN ura.amount END), 0) AS aluminumRecycled, " +
                    "COALESCE(SUM(CASE WHEN m.name = 'METALS' THEN ura.amount END), 0) AS metalsRecycled, " +
                    "COALESCE(SUM(CASE WHEN m.name = 'ELECTRONICS' THEN ura.amount END), 0) AS electronicsRecycled " +
                    "FROM user_recycling_activities ura " +
                    "JOIN materials m ON ura.material_id = m.material_id " +
                    "WHERE ura.user_id = :userId " +
                    "AND ura.created_at BETWEEN :startDate AND :endDate";
}
