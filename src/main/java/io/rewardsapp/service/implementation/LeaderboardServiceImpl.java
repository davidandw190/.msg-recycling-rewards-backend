package io.rewardsapp.service.implementation;

import io.rewardsapp.domain.User;
import io.rewardsapp.dto.LeaderboardEntryDTO;
import io.rewardsapp.repository.JpaUserRepository;
import io.rewardsapp.service.LeaderboardService;
import io.rewardsapp.service.RewardPointsService;
import io.rewardsapp.service.UserService;
import io.rewardsapp.specs.LeaderboardSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {
    private final UserService userService;
    private final RewardPointsService rewardPointsService;
    private final JpaUserRepository userRepository;



    @Override
    public List<LeaderboardEntryDTO> getLeaderboard(String county, int page, int size, String sortBy, String sortOrder) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<User> specification = LeaderboardSpecification.buildLeaderboardSpecification(county, sortBy, sortOrder);

        Page<User> userPage = userRepository.findAll(specification, pageable);

        return userPage.getContent().stream()
                .map(user -> {
                    if (user.getRewardPoints() != null) {
                        return LeaderboardEntryDTO.builder()
                                .userId(user.getId())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .email(user.getEmail())
                                .county(user.getCounty())
                                .city(user.getCity())
                                .imageUrl(user.getImageUrl())
                                .totalRewardPoints(user.getRewardPoints().getTotalPoints())
                                .build();
                    } else {
                        return LeaderboardEntryDTO.builder()
                                .userId(user.getId())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .email(user.getEmail())
                                .county(user.getCounty())
                                .city(user.getCity())
                                .imageUrl(user.getImageUrl())
                                .totalRewardPoints(0L)
                                .build();
                    }
                })
                .collect(Collectors.toList());
    }
}
