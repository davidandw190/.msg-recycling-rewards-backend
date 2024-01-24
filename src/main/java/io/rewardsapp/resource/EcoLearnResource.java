package io.rewardsapp.resource;

import io.rewardsapp.domain.HttpResponse;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.form.CreateEducationalResourceForm;
import io.rewardsapp.service.*;
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
import java.util.List;
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
    private final ContentTypeService contentTypeService;
    private final CategoryService categoryService;
    private final EducationalResourcesService educationalResourcesService;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpResponse> createResource(
            @AuthenticationPrincipal UserDTO authenticatedUser,
            @RequestBody @Valid CreateEducationalResourceForm form
    ) {
        educationalResourcesService.createEducationalResource(form.title(), form.content(), form.contentType(), form.media(), form.categories());
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
            case "LIKE"     -> educationalResourcesService.likeResource(authenticatedUser.id(), resourceId);
            case "SAVE"     -> educationalResourcesService.saveResource(authenticatedUser.id(), resourceId);
            case "SHARE"    -> educationalResourcesService.shareResource(authenticatedUser.id(), resourceId);

            default         -> handleException(request, response, new BadRequestException("Invalid engagement action: " + action.toUpperCase()));
        }

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", authenticatedUser))
                        .message( action + " performed successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
            );
    }

    @GetMapping("/search")
    public ResponseEntity<HttpResponse> searchCenters(
            @AuthenticationPrincipal UserDTO authenticatedUser,
            @RequestParam(defaultValue = "") String title,
            @RequestParam(defaultValue = "") String contentType,
            @RequestParam(defaultValue = "") List<String> categories,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,

            @RequestParam(defaultValue = "false") Boolean likedOnly,
            @RequestParam(defaultValue = "false") Boolean savedOnly
    ) {
        Map<String, Object> searchData = null;

        try {
            validatePageAndSize(page, size);

            searchData = Map.of(
                    "user", authenticatedUser,
                    "page", educationalResourcesService.searchResources(
                            toUser(authenticatedUser),
                            title,
                            contentType,
                            categories,
                            page,
                            size,
                            sortBy,
                            sortOrder,
                            likedOnly,
                            savedOnly
                    ));

        } catch (Exception exception) {
            handleException(request, response, exception);
        }

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(searchData)
                        .message("Resources retrieved successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @GetMapping("/content-types")
    public ResponseEntity<HttpResponse> availableContentTypes(@AuthenticationPrincipal UserDTO authenticatedUser) {

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "availableContentTypes", contentTypeService.getAvailableContentTypeNames()))
                        .message("Available content types retrieved successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @GetMapping("/categories")
    public ResponseEntity<HttpResponse> availableCategories(@AuthenticationPrincipal UserDTO authenticatedUser) {

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "availableCategories", categoryService.getAvailableCategoryNames()))
                        .message("Available categories retrieved successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    @GetMapping("/create")
    public ResponseEntity<HttpResponse> fetchForCreateResource(@AuthenticationPrincipal UserDTO authenticatedUser) {

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of(
                                "user", authenticatedUser,
                                "availableContentTypes", contentTypeService.getAvailableContentTypeNames(),
                                "availableCategories", categoryService.getAvailableCategoryNames()))
                        .message("Available categories retrieved successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
        );
    }

    private void validatePageAndSize(int page, int size) {
        if (page < 0 || size <= 0 || size > 100) {
            throw new ApiException("Invalid page or size parameters");
        }
    }
}
