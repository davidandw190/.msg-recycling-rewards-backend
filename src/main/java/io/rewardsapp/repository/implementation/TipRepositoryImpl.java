package io.rewardsapp.repository.implementation;

import io.rewardsapp.query.TipsQuery;
import io.rewardsapp.repository.TipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.rewardsapp.query.TipsQuery.*;

@Repository
public class TipRepositoryImpl implements TipRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public TipRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long count() {
        return jdbcTemplate.queryForObject(COUNT_ECO_TIPS_QUERY, new HashMap<>(), Long.class);
    }

    @Override
    public String getContentByTipId(Long tipId) {
        Map<String, Object> params = new HashMap<>();
        params.put("tipId", tipId);
        return jdbcTemplate.queryForObject(GET_CONTENT_BY_TIP_ID_QUERY, params, String.class);
    }

    @Override
    public Long getRandomTipId() {
        return jdbcTemplate.queryForObject(GET_RANDOM_TIP_ID, new HashMap<>(), Long.class);
    }


}