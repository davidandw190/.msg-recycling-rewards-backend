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
import org.apache.coyote.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

import static io.rewardsapp.dto.mapper.UserDTOMapper.toUser;
import static io.rewardsapp.utils.ExceptionUtils.handleException;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

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

    @PostMapping(value = "/engage/{action}/{resourceId}")
    public ResponseEntity<HttpResponse> engageResource(
            @AuthenticationPrincipal UserDTO authenticatedUser,
            @PathVariable String action,
            @PathVariable Long resourceId
    ) {
        switch (action.toUpperCase()) {
            case "LIKE"     -> educationalResourcesService.likeResource(toUser(authenticatedUser), resourceId);
            case "SAVE"     -> educationalResourcesService.saveResource(toUser(authenticatedUser), resourceId);
            case "SHARE"    -> educationalResourcesService.shareResource(toUser(authenticatedUser), resourceId);

            default         -> handleException(request, response, new BadRequestException("Invalid engagement action: " + action.toUpperCase()));
        }

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", authenticatedUser))
                        .message( action + " performed successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
            );
    }
}
