package io.rewardsapp.query;

public class TipsQuery {
    public static final String COUNT_ECO_TIPS_QUERY = "SELECT COUNT(*) FROM eco_tips";
    public static final String GET_CONTENT_BY_TIP_ID_QUERY = "SELECT content FROM eco_tips WHERE tip_id = :tipId";
    public static final String GET_RANDOM_TIP_ID = "SELECT tip_id FROM eco_tips ORDER BY RANDOM() LIMIT 1";

}
