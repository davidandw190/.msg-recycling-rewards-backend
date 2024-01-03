package io.rewardsapp.query;

public class StatsQuery {

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

    public static final String GET_CENTER_TOTAL_STATS =
            "SELECT " +
                    "COUNT(DISTINCT ura.user_id) AS recyclersNumber, " +
                    "COUNT(ura.activity_id) AS activitiesNumber, " +
                    "COALESCE(SUM(CASE WHEN m.name = 'paper' THEN ura.amount END), 0) AS paperRecycled, " +
                    "COALESCE(SUM(CASE WHEN m.name = 'plastic' THEN ura.amount END), 0) AS plasticRecycled, " +
                    "COALESCE(SUM(CASE WHEN m.name = 'glass' THEN ura.amount END), 0) AS glassRecycled, " +
                    "COALESCE(SUM(CASE WHEN m.name = 'aluminum' THEN ura.amount END), 0) AS aluminumRecycled, " +
                    "COALESCE(SUM(CASE WHEN m.name = 'metals' THEN ura.amount END), 0) AS metalsRecycled, " +
                    "COALESCE(SUM(CASE WHEN m.name = 'electronics' THEN ura.amount END), 0) AS electronicsRecycled " +
                    "FROM user_recycling_activities ura " +
                    "LEFT JOIN recyclable_materials m ON ura.material_id = m.material_id " +
                    "WHERE ura.center_id = :centerId";

    public static final String GET_APP_TOTAL_STATS =
            "SELECT COUNT(DISTINCT u.user_id) AS activeRecyclersNumber, " +
                    "COALESCE(SUM(rp.total_points), 0) AS monthlyRewardPoints " +
                    "FROM users u " +
                    "LEFT JOIN reward_points rp ON u.user_id = rp.user_id";
}
