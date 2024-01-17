package io.rewardsapp.service.implementation;

import io.rewardsapp.domain.User;
import io.rewardsapp.dto.LeaderboardEntryDTO;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.repository.JpaUserRepository;
import io.rewardsapp.service.LeaderboardService;
import io.rewardsapp.specs.LeaderboardSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {
    private final JpaUserRepository userRepository;

    /**
     * Retrieves a paginated list of leaderboard entries based on specified criteria.
     *
     * @param county     The county for which the leaderboard entries are to be fetched.
     * @param page       The page number (zero-based) of the leaderboard results to retrieve.
     * @param size       The number of leaderboard entries to retrieve per page.
     * @param sortBy     The field by which the results should be sorted.
     * @param sortOrder  The order in which the results should be sorted (asc or desc).
     * @return A paginated list of leaderboard entries.
     * @throws ApiException If an error occurs during the retrieval process.
     */
    @Override
    public Page<LeaderboardEntryDTO> getLeaderboard(String county, int page, int size, String sortBy, String sortOrder) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
            Specification<User> specification = LeaderboardSpecification.buildLeaderboardSpecification(county, sortBy, sortOrder);
            Page<User> userPage = userRepository.findAll(specification, pageable);

            return userPage.map(this::mapToLeaderboardEntryDTO);

        } catch (Exception exception) {
            throw new ApiException("Error occurred while fetching leaderboard. Please try again later." + exception.getMessage());
        }
    }


    /**
     * Maps a User entity to a LeaderboardEntryDTO.
     *
     * @param user The user entity to be mapped.
     * @return The leaderboard entry DTO representing the user.
     */
    private LeaderboardEntryDTO mapToLeaderboardEntryDTO(User user) {
        return LeaderboardEntryDTO.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .county(user.getCounty())
                .city(user.getCity())
                .imageUrl(user.getImageUrl())
                .rewardPoints(user.getRewardPoints() != null
                        ? user.getRewardPoints().getTotalPoints()
                        : 0L)
                .build();
    }
}
