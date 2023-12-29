package io.rewardsapp.resource;

import io.rewardsapp.domain.*;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.form.CreateCenterForm;
import io.rewardsapp.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private final RecyclingActivityService activityService;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    @GetMapping("/list-all")
    public ResponseEntity<HttpResponse> listAllRecyclingCenters(
            @AuthenticationPrincipal UserDTO authenticatedUser,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> size) {

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", userService.getUser(authenticatedUser.id()),
                                "page", centerService.getCenters(page.orElse(0), size.orElse(10)),
                                "userStats", statsService.getUserStatsForLastMonth(authenticatedUser.id())
                                ))
                        .message("Recycling centers retrieved successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

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


    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpResponse> createCustomer(@AuthenticationPrincipal UserDTO authenticatedUser, @RequestBody @Valid CreateCenterForm form) {
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

    @GetMapping("/get/{id}")
    public ResponseEntity<HttpResponse> getCustomer(@AuthenticationPrincipal UserDTO authenticatedUser, @PathVariable("id") Long id) {
        RecyclingCenter center = centerService.getCenter(id);
        User user = toUser(userService.getUser(authenticatedUser.id()));
        List<UserRecyclingActivity> activities = activityService.getUserRecyclingActivitiesAtCenter(user, center);

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", user,
                                "center", center,
                                "activities", activities))
                        .message("Center retrieved successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @PutMapping("/update")
    public ResponseEntity<HttpResponse> updateCustomer(@AuthenticationPrincipal UserDTO authenticatedUser, @RequestBody RecyclingCenter center) {
        RecyclingCenter updatedCenter = centerService.updateCenter(center.getCenterId());
        User user = toUser(userService.getUser(authenticatedUser.id()));
        List<UserRecyclingActivity> activities = activityService.getUserRecyclingActivitiesAtCenter(user, center);

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", user,
                                "center", updatedCenter,
                                "activities", activities))
                        .message("Customer updated successfully!")
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
