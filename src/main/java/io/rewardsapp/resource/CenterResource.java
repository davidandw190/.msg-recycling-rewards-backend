package io.rewardsapp.resource;

import io.rewardsapp.domain.*;
import io.rewardsapp.domain.auth.Role;
import io.rewardsapp.domain.auth.User;
import io.rewardsapp.domain.recycling.RecyclingCenter;
import io.rewardsapp.domain.recycling.UserRecyclingActivity;
import io.rewardsapp.dto.CenterStatsDTO;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.form.CreateCenterForm;
import io.rewardsapp.form.CreateRecyclingActivityForm;
import io.rewardsapp.form.UpdateCenterForm;
import io.rewardsapp.report.CenterReport;
import io.rewardsapp.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.rewardsapp.dto.mapper.UserDTOMapper.toUser;
import static io.rewardsapp.utils.ExceptionUtils.handleException;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.parseMediaType;

@Slf4j
@RestController
@RequestMapping(path = "/centers")
@RequiredArgsConstructor
public class CenterResource {
    private final UserService userService;
    private final CenterService centerService;
    private final StatsService statsService;
    private final RoleService roleService;
    private final MaterialsService materialsService;
    private final RewardPointsService rewardPointsService;
    private final RecyclingActivityService activityService;
    private final TipsService tipsService;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    /**
     * Retrieves a list of all recycling centers, user details, and statistics for the authenticated user.
     *
     * @param authenticatedUser The authenticated user details.
     * @param page              Optional parameter for pagination (default: 0).
     * @param size              Optional parameter for page size (default: 10).
     * @return ResponseEntity with the list of recycling centers, user details, and statistics.
     */
    @GetMapping("/list-all")
    public ResponseEntity<HttpResponse> listAllRecyclingCenters(
            @AuthenticationPrincipal UserDTO authenticatedUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", authenticatedUser,
                                "page", centerService.getCenters(page, size),
                                "userStats", statsService.getUserStatsForLastMonth(authenticatedUser.id())
                        ))
                        .message("Recycling centers retrieved successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    /**
     * Retrieves a list of recycling centers near the authenticated user's location.
     *
     * @param authenticatedUser The authenticated user details.
     * @param page              Page number for pagination.
     * @param size              Page size for pagination.
     * @param sortBy            Sorting field (default: createdAt).
     * @param sortOrder         Sorting order (default: asc).
     * @return ResponseEntity with the list of recycling centers near the user, user statistics and app statistics.
     */
    @GetMapping("/list-nearby")
    public ResponseEntity<HttpResponse> listRecyclingCentersNearby(
            @AuthenticationPrincipal UserDTO authenticatedUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ) {
        Map<String, Object> searchData = null;
        try {

            searchData = Map.of(
                    "user", authenticatedUser,
                    "page", centerService.searchCenters(
                            "",
                            authenticatedUser.county(),
                            authenticatedUser.city(),
                            new ArrayList<>(),
                            page,
                            size,
                            sortBy,
                            sortOrder
                    ),
                    "userRewardPoints", rewardPointsService.getRewardPointsAmount(authenticatedUser.id()),
                    "userStats", statsService.getUserStatsForLastMonth(authenticatedUser.id()),
                    "appStats", statsService.getAppStats(),
                    "ecoTip", tipsService.getRandomRecyclingTip()
            );
        } catch (Exception exception) {
            handleException(request, response, exception);
        }

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(searchData)
                        .message("Centers retrieved successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    /**
     * Searches for recycling centers based on specified criteria and retrieves the results.
     *
     * @param authenticatedUser The authenticated user details.
     * @param name              Center name (optional).
     * @param county            Center county (optional).
     * @param city              Center city (optional).
     * @param materials         List of accepted materials (optional).
     * @param page              Page number for pagination.
     * @param size              Page size for pagination.
     * @param sortBy            Sorting field (default: createdAt).
     * @param sortOrder         Sorting order (default: asc).
     * @return ResponseEntity with the search results.
     */
    @GetMapping("/search")
    public ResponseEntity<HttpResponse> searchCenters(
            @AuthenticationPrincipal UserDTO authenticatedUser,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String county,
            @RequestParam(defaultValue = "") String city,
            @RequestParam(defaultValue = "") List<String> materials,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ) {
        Map<String, Object> searchData = null;
        try {
            validatePageAndSize(page, size);

            searchData = Map.of(
                    "user", authenticatedUser,
                    "page", centerService.searchCenters(name, county, city, materials, page, size, sortBy, sortOrder)
            );
        } catch (Exception exception) {
            handleException(request, response, exception);
        }

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(searchData)
                        .message("Centers retrieved successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    /**
     * Retrieves a list of all materials accepted by recycling centers.
     *
     * @return ResponseEntity with the list of materials.
     */
    @GetMapping("/materials/all")
    public ResponseEntity<HttpResponse> getAllMaterials() {
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("materials", materialsService.getAllMaterials()))
                        .message("Materials retrieved successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    /**
     * Creates a new recycling center and returns the details.
     *
     * @param authenticatedUser The authenticated user details.
     * @param form              Form containing details for creating a new center.
     * @return ResponseEntity with the created center details and the user.
     */
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpResponse> createCustomer(
            @AuthenticationPrincipal UserDTO authenticatedUser,
            @RequestBody @Valid CreateCenterForm form
    ) {
        return ResponseEntity.created(URI.create(""))
                .body(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of(
                                        "user", authenticatedUser,
                                        "center", centerService.createCenter(form)))
                                .message("Recycling center created successfully!")
                                .status(CREATED)
                                .statusCode(CREATED.value())
                                .build()
                );
    }

    /**
     * Retrieves details for a specific recycling center, user, activities, and reward points.
     *
     * @param authenticatedUser The authenticated user details.
     * @param centerId          ID of the recycling center.
     * @return ResponseEntity with the center details, user details, recycling activities of the user to the center,
     * user reward points for the last month and center statistics.
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<HttpResponse> getCenter(
            @AuthenticationPrincipal UserDTO authenticatedUser,
            @PathVariable("id") Long centerId
    ) {
        RecyclingCenter center = centerService.getCenter(centerId);
        List<UserRecyclingActivity> activities = activityService.getUserRecyclingActivitiesAtCenter(toUser(authenticatedUser), center);
        Long rewardPoints = rewardPointsService.getRewardPointsAmount(authenticatedUser.id());
        CenterStatsDTO centerStats = statsService.getCenterTotalStats(centerId);

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", authenticatedUser,
                                "center", center,
                                "activities", activities,
                                "rewardPoints", rewardPoints,
                                "centerStats", centerStats
                        ))
                        .message("Center retrieved successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    /**
     * Updates the details of a recycling center and returns the updated details.
     *
     * @param authenticatedUser The authenticated user details.
     * @param form              Form containing details for updating a center.
     * @return ResponseEntity with the updated center details.
     */
    @PutMapping("/update")
    public ResponseEntity<HttpResponse> updateCenter(
            @AuthenticationPrincipal UserDTO authenticatedUser,
            @RequestBody UpdateCenterForm form
    ) {
        RecyclingCenter updatedCenter = centerService.updateCenter(form);
        List<UserRecyclingActivity> activities = activityService.getUserRecyclingActivitiesAtCenter(toUser(authenticatedUser), updatedCenter);
        Long rewardPoints = rewardPointsService.getRewardPointsAmount(authenticatedUser.id());
        CenterStatsDTO centerStats = statsService.getCenterTotalStats(form.centerId());


        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", authenticatedUser,
                                "center", updatedCenter,
                                "activities", activities,
                                "rewardPoints", rewardPoints,
                                "centerStats", centerStats)
                        )
                        .message("Center updated successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    /**
     * Records a user's contribution to recycling activities and returns the updated details.
     *
     * @param authenticatedUser The authenticated user details.
     * @param form              Form containing details for the recycling activity.
     * @return ResponseEntity with the updated user, center, reward points, and activities details.
     */
    @PostMapping("/contribute")
    public ResponseEntity<HttpResponse> contribute(
            @AuthenticationPrincipal UserDTO authenticatedUser,
            @RequestBody CreateRecyclingActivityForm form
    ) {
        int vouchersEarned = activityService.createActivity(form);

        RecyclingCenter updatedCenter = centerService.getCenter(form.centerId());
        User user = toUser(userService.getUser(authenticatedUser.id()));
        Role userRole = roleService.getRoleByUserId(authenticatedUser.id());
        List<UserRecyclingActivity> activities = activityService.getUserRecyclingActivitiesAtCenter(user, updatedCenter);
        Long rewardPoints = rewardPointsService.getRewardPointsAmount(authenticatedUser.id());

        String voucherMessage = (vouchersEarned == 1) ? "voucher" : "vouchers";
        String contributionMessage = (vouchersEarned > 0)
                ? String.format("Your contribution was received successfully, and you earned %d %s!", vouchersEarned, voucherMessage)
                : "Your contribution was received!";

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", user,
                                "userRole", userRole,
                                "center", updatedCenter,
                                "rewardPoints", rewardPoints,
                                "activities", activities))
                        .message(contributionMessage)
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/download/report")
    public ResponseEntity<Resource> downloadReport() {
        List<RecyclingCenter> centers = new ArrayList<>();
        centerService.getCenters().iterator().forEachRemaining(centers::add);
        CenterReport report = new CenterReport(centers);
        HttpHeaders headers = new HttpHeaders();
        headers.add("File-Name", "centers-report.xlsx");
        headers.add(CONTENT_DISPOSITION, "attachment;File-Name=customer-report.xlsx");
        return ResponseEntity.ok().contentType(parseMediaType("application/vnd.ms-excel"))
                .headers(headers).body(report.export());
    }



    private void validatePageAndSize(int page, int size) {
        if (page < 0 || size <= 0 || size > 100) {
            throw new ApiException("Invalid page or size parameters");
        }
    }
}
