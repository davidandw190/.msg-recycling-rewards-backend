package io.rewardsapp.service.implementation;

import io.rewardsapp.domain.auth.User;
import io.rewardsapp.dto.LeaderboardEntryDTO;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.repository.JpaUserRepository;
import io.rewardsapp.service.LeaderboardService;
import io.rewardsapp.service.RoleService;
import io.rewardsapp.specs.LeaderboardSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {
    private final JpaUserRepository userRepository;
    private final RoleService roleService;

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
            Pageable pageable = PageRequest.of(page, size);
            Specification<User> specification = LeaderboardSpecification.buildLeaderboardSpecification(county, sortBy, sortOrder);

            Page<User> userPage = userRepository.findAll(specification, pageable);

            List<LeaderboardEntryDTO> leaderboardEntries = userPage.getContent().stream()
                    .map(this::mapToLeaderboardEntryDTO)
                    .collect(Collectors.toList());

            if ("rewardPoints".equals(sortBy)) {
                Comparator<LeaderboardEntryDTO> comparator = Comparator.comparing(LeaderboardEntryDTO::getRewardPoints);
                if ("desc".equalsIgnoreCase(sortOrder)) {
                    comparator = comparator.reversed();
                }
                leaderboardEntries.sort(comparator);
            }

            calculateRank(leaderboardEntries);

            return PageableExecutionUtils.getPage(leaderboardEntries, pageable, userPage::getTotalElements);

        } catch (Exception exception) {
            throw new ApiException("Error occurred while fetching leaderboard. Please try again later." + exception.getMessage());
        }
    }

    private void calculateRank(List<LeaderboardEntryDTO> leaderboardEntries) {
        List<LeaderboardEntryDTO> sortedEntries = leaderboardEntries.stream()
                .sorted((entry1, entry2) -> Long.compare(entry2.getRewardPoints(), entry1.getRewardPoints()))
                .toList();

        long rank = 1L;
        for (LeaderboardEntryDTO entry : sortedEntries) {
            if (!entry.isAdministration()) {
                entry.setRank(rank++);
            }
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
                .administration(roleService.checkIfIsAdministrative(user.getId()))
                .build();
    }
}
