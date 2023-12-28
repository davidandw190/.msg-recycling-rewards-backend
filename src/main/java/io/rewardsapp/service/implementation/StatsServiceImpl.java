package io.rewardsapp.service.implementation;

import io.rewardsapp.domain.UserRecyclingActivity;
import io.rewardsapp.dto.UserStatsDTO;
import io.rewardsapp.repository.UserRecyclingActivityRepository;
import io.rewardsapp.service.StatsService;
import io.rewardsapp.specs.UserRecyclingActivitySpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the StatsService interface providing methods for retrieving statistics.
 */
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final UserRecyclingActivityRepository activityRepository;

    @Override
    public UserStatsDTO getUserStatsForLastMonth(Long userId) {
        return null;
    }
}