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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static io.rewardsapp.dto.mapper.UserDTOMapper.toUser;
import static io.rewardsapp.utils.ExceptionUtils.handleException;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

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


    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HttpResponse> createResource(
            @AuthenticationPrincipal UserDTO authenticatedUser,
            @RequestParam("title") String title,
            @RequestParam("contentType") String contentType,
            @RequestParam("content") String content,
            @RequestParam("categories") String[] categories,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        educationalResourcesService.createEducationalResource(title, content, contentType, categories, file);

        return ResponseEntity.created(getUri())
                .body(HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", authenticatedUser))
                        .message("Eco-Learn " + contentType.toUpperCase() + " created successfully!")
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build());
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
                        .message( action.toUpperCase() + " performed successfully!")
                        .status(OK)
                        .statusCode(OK.value())
                        .build()
            );
    }

    @GetMapping(value = "resource/image/{fileName}", produces = IMAGE_PNG_VALUE)
    public byte[] getResourceImage(@PathVariable("fileName") String fileName) throws Exception {
        return Files.readAllBytes(Paths.get(System.getProperty("user.home") + "/Downloads/images/" + fileName));
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

    private URI getUri() {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/eco-learn/resource/image/get/<resourceId>").toUriString());
    }
}
