package io.rewardsapp.resource;

import io.rewardsapp.domain.HttpResponse;
import io.rewardsapp.domain.RecyclingCenter;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.service.RecyclingCenterService;
import io.rewardsapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RequestMapping(path = "/centers")
@RequiredArgsConstructor
public class RecyclingCenterResource {
    private final UserService userService;
    private final RecyclingCenterService centerService;

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
                                "page", centerService.getRecyclingCenters(page.orElse(0), size.orElse(10))))
                        .message("Recycling centers retrieved successfully!")
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
                                        "center", centerService.createRecyclingCenter(newCenter)))
                                .message("Recycling center created successfully!")
                                .status(CREATED)
                                .statusCode(CREATED.value())
                                .build()
                );
    }
}
