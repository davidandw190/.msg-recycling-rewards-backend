package io.rewardsapp.resource;

import io.rewardsapp.domain.HttpResponse;
import io.rewardsapp.domain.RecyclingCenter;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.service.CenterService;
import io.rewardsapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.rewardsapp.utils.ExceptionUtils.handleException;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "/centers")
@RequiredArgsConstructor
public class CenterResource {
    private final UserService userService;
    private final CenterService centerService;
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
                                "user", userService.getUserById(authenticatedUser.id()),
                                "page", centerService.getCenters(page.orElse(0), size.orElse(10))))
                        .message("Recycling centers retrieved successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<HttpResponse> searchCenters(
            @AuthenticationPrincipal UserDTO user,
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
                    "user", userService.getUserByEmail(user.email()),
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


    @PostMapping("/create")
    public ResponseEntity<HttpResponse> createCustomer(@AuthenticationPrincipal UserDTO authenticatedUser, @RequestBody RecyclingCenter newCenter) {
        return ResponseEntity.created(URI.create(""))
                .body(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of(
                                        "user", userService.getUserById(authenticatedUser.id()),
                                        "center", centerService.createCenter(newCenter)))
                                .message("Recycling center created successfully!")
                                .status(CREATED)
                                .statusCode(CREATED.value())
                                .build()
                );
    }

    private void validatePageAndSize(int page, int size) throws BadRequestException {
        if (page < 0 || size <= 0 || size > 100) {
            throw new BadRequestException("Invalid page or size parameters");
        }
    }
}