package io.rewardsapp.resource;

import io.rewardsapp.domain.HttpResponse;
import io.rewardsapp.dto.LeaderboardEntryDTO;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.service.LeaderboardService;
import io.rewardsapp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping(path = "/leaderboard")
@RequiredArgsConstructor
public class LeaderboardResource {
    private final UserService userService;
    private final LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<HttpResponse> listLeaderboard(
            @AuthenticationPrincipal UserDTO authenticatedUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String county,
            @RequestParam(defaultValue = "rewardPoints") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        List<LeaderboardEntryDTO> leaderboard = leaderboardService.getLeaderboard(county, page, size, sortBy, sortOrder);

        Map<String, Object> searchData = Map.of("user", authenticatedUser, "leaderboard", leaderboard);

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(searchData)
                        .message("Leaderboard retrieved successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }
}
