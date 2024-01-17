package io.rewardsapp.resource;

import io.rewardsapp.domain.HttpResponse;
import io.rewardsapp.dto.LeaderboardEntryDTO;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.service.LeaderboardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping(path = "/leaderboard")
@RequiredArgsConstructor
public class LeaderboardResource {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final LeaderboardService leaderboardService;

    /**
     * Retrieves a paginated list of leaderboard entries for the specified county and sorting criteria.
     *
     * @param authenticatedUser The authenticated user making the request.
     * @param page              The page number (zero-based) of the leaderboard results to retrieve.
     * @param size              The number of leaderboard entries to retrieve per page.
     * @param county            The county for which the leaderboard entries are to be fetched.
     * @param sortBy            The field by which the results should be sorted.
     * @param sortOrder         The order in which the results should be sorted (asc or desc).
     * @return A response entity containing the leaderboard entries and additional information.
     */
    @GetMapping
    public ResponseEntity<HttpResponse> listLeaderboard(
            @AuthenticationPrincipal UserDTO authenticatedUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String county,
            @RequestParam(defaultValue = "rewardPoints") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        Page<LeaderboardEntryDTO> leaderboard = leaderboardService.getLeaderboard(county, page, size, sortBy, sortOrder);

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", authenticatedUser,
                                "page", leaderboard)
                        )
                        .message("Leaderboard retrieved successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }
}
