package io.rewardsapp.query;

public class UserStatsQuery {
    public static final String GET_USER_STATS_FOR_LAST_MONTH =
            "SELECT " +
                    "SUM(CASE WHEN m.name = 'PAPER' THEN ura.amount ELSE 0 END) AS paperRecycled, " +
                    "SUM(CASE WHEN m.name = 'PLASTIC' THEN ura.amount ELSE 0 END) AS plasticRecycled, " +
                    "SUM(CASE WHEN m.name = 'GLASS' THEN ura.amount ELSE 0 END) AS glassRecycled, " +
                    "SUM(CASE WHEN m.name = 'ALUMINUM' THEN ura.amount ELSE 0 END) AS aluminumRecycled, " +
                    "SUM(CASE WHEN m.name = 'METALS' THEN ura.amount ELSE 0 END) AS metalsRecycled, " +
                    "SUM(CASE WHEN m.name = 'ELECTRONICS' THEN ura.amount ELSE 0 END) AS electronicsRecycled " +
                    "FROM user_recycling_activities ura " +
                    "JOIN recyclable_materials m ON ura.material_id = m.material_id " +
                    "WHERE ura.user_id = :userId " +
                    "AND ura.created_at BETWEEN :startDate AND :endDate";
}
