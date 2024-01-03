package io.rewardsapp.resource;

import io.rewardsapp.domain.*;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.form.CreateCenterForm;
import io.rewardsapp.form.CreateRecyclingActivityForm;
import io.rewardsapp.form.UpdateCenterForm;
import io.rewardsapp.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

import static io.rewardsapp.dto.mapper.UserDTOMapper.toUser;
import static io.rewardsapp.utils.ExceptionUtils.handleException;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "/centers")
@RequiredArgsConstructor
public class CenterResource {
    private final UserService userService;
    private final CenterService centerService;
    private final StatsService statsService;
    private final MaterialsService materialsService;
    private final RewardPointsService rewardPointsService;
    private final RecyclingActivityService activityService;
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
                                "user", userService.getUser(authenticatedUser.id()),
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
     * @return ResponseEntity with the list of recycling centers near the user.
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
                    "user", userService.getUser(authenticatedUser.id()),
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
                    "userStats", statsService.getUserStatsForLastMonth(authenticatedUser.id())
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
                    "user", userService.getUser(authenticatedUser.id()),
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
     * @return ResponseEntity with the created center details.
     */
    @Transactional
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
                                        "user", userService.getUser(authenticatedUser.id()),
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
     * @param id                ID of the recycling center.
     * @return ResponseEntity with the center details, user details, activities, and reward points.
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<HttpResponse> getCustomer(
            @AuthenticationPrincipal UserDTO authenticatedUser,
            @PathVariable("id") Long id
    ) {
        RecyclingCenter center = centerService.getCenter(id);
        User user = toUser(userService.getUser(authenticatedUser.id()));
        List<UserRecyclingActivity> activities = activityService.getUserRecyclingActivitiesAtCenter(user, center);
        Long rewardPoints = rewardPointsService.getRewardPointsAmount(authenticatedUser.id());

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", user,
                                "center", center,
                                "activities", activities,
                                "rewardPoints", rewardPoints))
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
    @Transactional
    @PutMapping("/update")
    public ResponseEntity<HttpResponse> updateCenter(
            @AuthenticationPrincipal UserDTO authenticatedUser,
            @RequestBody UpdateCenterForm form
    ) {
        RecyclingCenter updatedCenter = centerService.updateCenter(form);
        User user = toUser(userService.getUser(authenticatedUser.id()));
        List<UserRecyclingActivity> activities = activityService.getUserRecyclingActivitiesAtCenter(user, updatedCenter);
        Long rewardPoints = rewardPointsService.getRewardPointsAmount(authenticatedUser.id());


        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", user,
                                "center", updatedCenter,
                                "activities", activities,
                                "rewardPoints", rewardPoints))
                        .message("Center updated successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    /**
     * Records a user's contribution to recycling activities and returns the updated details.
     *
     * @param authenticatedUser The authenticated user details.
     * @param form              Form containing details for the recycling activity.
     * @return ResponseEntity with the updated user, center, reward points, and activities details.
     */
    @Transactional
    @PostMapping("/contribute")
    public ResponseEntity<HttpResponse> contribute(@AuthenticationPrincipal UserDTO authenticatedUser, @RequestBody CreateRecyclingActivityForm form) {

        activityService.createActivity(form);

        RecyclingCenter updatedCenter = centerService.getCenter(form.centerId());
        User user = toUser(userService.getUser(authenticatedUser.id()));
        List<UserRecyclingActivity> activities = activityService.getUserRecyclingActivitiesAtCenter(user, updatedCenter);
        Long rewardPoints = rewardPointsService.getRewardPointsAmount(authenticatedUser.id());


        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", user,
                                "center", updatedCenter,
                                "rewardPoints", rewardPoints,
                                "activities", activities))
                        .message("You contribution was recieved!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    private void validatePageAndSize(int page, int size) throws BadRequestException {
        if (page < 0 || size <= 0 || size > 100) {
            throw new BadRequestException("Invalid page or size parameters");
        }
    }
}
