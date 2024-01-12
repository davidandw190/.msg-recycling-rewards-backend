package io.rewardsapp.repository;

public interface TipRepository {
    Long count();

    String getContentByTipId(Long tipId);

    Long getRandomTipId();
}