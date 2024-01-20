package io.rewardsapp.resource;

import io.rewardsapp.domain.HttpResponse;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.form.CreateEducationalResourceForm;
import io.rewardsapp.service.EducationalResourcesService;
import io.rewardsapp.service.RoleService;
import io.rewardsapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;

@Slf4j
@RestController
@RequestMapping(path = "/eco-learn")
@RequiredArgsConstructor
public class EcoLearnResource {
    private final UserService userService;
    private final RoleService roleService;
    private final EducationalResourcesService educationalResourcesService;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpResponse> createCustomer(
            @AuthenticationPrincipal UserDTO authenticatedUser,
            @RequestBody @Valid CreateEducationalResourceForm form
    ) {
        educationalResourcesService.createEducationalResource(form.title(), form.content(), form.contentType(), form.categories());
        return ResponseEntity.created(URI.create(""))
                .body(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of(
                                        "user", authenticatedUser))
                                .message("Eco-Learn " + form.contentType().toUpperCase() + " created successfully!")
                                .status(CREATED)
                                .statusCode(CREATED.value())
                                .build()
                );
    }
}
