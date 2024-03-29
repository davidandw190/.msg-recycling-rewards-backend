package io.rewardsapp.service.implementation;

import io.rewardsapp.domain.auth.User;
import io.rewardsapp.domain.educational.Category;
import io.rewardsapp.domain.educational.ContentType;
import io.rewardsapp.domain.educational.EducationalResource;
import io.rewardsapp.domain.educational.UserEngagement;
import io.rewardsapp.dto.EducationalResourceDTO;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.repository.CategoryRepository;
import io.rewardsapp.repository.ContentTypeRepository;
import io.rewardsapp.repository.EducationalResourceRepository;
import io.rewardsapp.repository.UserEngagementRepository;
import io.rewardsapp.service.EducationalResourcesService;
import io.rewardsapp.service.UserService;
import io.rewardsapp.specs.EducationalResourceSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

@Slf4j
@Service
@RequiredArgsConstructor
public class EducationalResourcesServiceImpl implements EducationalResourcesService {

    private final EducationalResourceRepository educationalResourceRepository;
    private final UserEngagementRepository userEngagementRepository;
    private final ContentTypeRepository contentTypeRepository;
    private final CategoryRepository categoryRepository;

    private final UserService userService;

    /**
     * Creates a new eco-learn educational resource with the provided information.
     *
     * @param title             The title of the educational resource.
     * @param content           The content of the educational resource.
     * @param contentTypeName   The name of the content type for the educational resource.
     * @param categoryNames     The names of the categories associated with the educational resource.
     * @return The created educational resource.
     * @throws ApiException If there is an issue creating the educational resource.
     */
    @Transactional
    @Override
    public EducationalResource createEducationalResource(
            String title,
            String content,
            String contentTypeName,
            String[] categoryNames,
            boolean isExternalMedia,
            String externalMediaUrl,
            MultipartFile file
    ) {
        ContentType contentType = findContentTypeByName(contentTypeName);
        Set<Category> categories = findCategoriesByNames(categoryNames);

        EducationalResource educationalResource = buildEducationalResource(
                title, content, isExternalMedia, contentType, categories
        );

        EducationalResource savedResource = educationalResourceRepository.save(educationalResource);

        if (isExternalMedia) {
            validateAndSetExternalMediaUrl(savedResource, externalMediaUrl);
        } else {
            attachMedia(savedResource, file, contentTypeName.equals("VIDEO"));
        }

        return educationalResourceRepository.save(savedResource);
    }

    private EducationalResource buildEducationalResource(
            String title,
            String content,
            boolean isExternalMedia,
            ContentType contentType,
            Set<Category> categories
    ) {
        return EducationalResource.builder()
                .title(title)
                .content(content)
                .isExternalMedia(isExternalMedia)
                .contentType(contentType)
                .likesCount(0L)
                .createdAt(LocalDateTime.now())
                .categories(categories)
                .build();
    }

    private void validateAndSetExternalMediaUrl(EducationalResource resource, String externalMediaUrl) {
        if (externalMediaUrl == null || externalMediaUrl.isEmpty()) {
            throw new ApiException("Not valid external media URL provided.");
        }
        resource.setMediaUrl(externalMediaUrl);
    }

    private void attachMedia(EducationalResource resource, MultipartFile mediaFile, boolean isVideo) {
        if (mediaFile != null && !mediaFile.isEmpty()) {
            try {
                String fileName = isVideo ? "videos" : "images";
                Path fileStorageLocation = Paths.get(System.getProperty("user.home") + "/Downloads/" + fileName + "/").toAbsolutePath().normalize();
                Files.createDirectories(fileStorageLocation);
                String extension = getFileExtension(Objects.requireNonNull(mediaFile.getOriginalFilename()));
                String newFileName = resource.getId() + extension;
                Path targetLocation = fileStorageLocation.resolve(newFileName);
                Files.copy(mediaFile.getInputStream(), targetLocation, REPLACE_EXISTING);
                resource.setMediaUrl(fromCurrentContextPath().path("/eco-learn/resource/" + fileName + "/" + newFileName).toUriString());
            } catch (Exception ex) {
                throw new ApiException("Could not store file " + mediaFile.getOriginalFilename() + ". Please try again!");
            }
        }
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }


    @Transactional
    @Override
    public Page<EducationalResourceDTO> searchResources(
            User user,
            String title,
            String contentType,
            List<String> categories,
            int page,
            int size,
            String sortBy,
            String sortOrder,
            boolean likedOnly,
            boolean savedOnly
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<EducationalResource> specification = EducationalResourceSpecification.searchResources(title, contentType, categories, likedOnly, savedOnly);
        Page<EducationalResource> resourcesPage = educationalResourceRepository.findAll(specification, pageable);

        List<EducationalResourceDTO> resourceDTOs = resourcesPage.getContent().stream()
                .map(resource -> convertToDTO(resource, user))
                .collect(Collectors.toList());

        return new PageImpl<>(resourceDTOs, pageable, resourcesPage.getTotalElements());
    }

    /**
     * Allows a user to like a specific educational resource.
     *
     * @param userId     The ID of the user who is performing the action.
     * @param resourceId The ID of the educational resource being liked.
     * @throws ApiException If there is an issue performing the action.
     */
    @Transactional
    @Override
    public void likeResource(Long userId, Long resourceId) {
        EducationalResource resource = getResourceById(resourceId);
        updateUserEngagement(userId, resource, true, false, false);
        log.info("User with ID {} liked educational resource with ID {}", userId, resourceId);
    }

    /**
     * Allows a user to save a specific educational resource.
     *
     * @param userId     The ID of the user who is performing the action.
     * @param resourceId The ID of the educational resource being saved.
     * @throws ApiException If there is an issue performing the action.
     */
    @Transactional
    @Override
    public void saveResource(Long userId, Long resourceId) {
        EducationalResource resource = getResourceById(resourceId);
        updateUserEngagement(userId, resource, false, false, true);
        log.info("User with ID {} saved educational resource with ID {}", userId, resourceId);
    }

    /**
     * Allows a user to share a specific educational resource.
     *
     * @param userId     The ID of the user who is performing the action.
     * @param resourceId The ID of the educational resource being shared.
     * @throws ApiException If there is an issue performing the action.
     */
    @Transactional
    @Override
    public void shareResource(Long userId, Long resourceId) {
        EducationalResource resource = getResourceById(resourceId);
        updateUserEngagement(userId, resource, false, true, false);
        log.info("User with ID {} shared educational resource with ID {}", userId, resourceId);

    }

    private EducationalResourceDTO convertToDTO(EducationalResource resource, User user) {
        long likesCount = userEngagementRepository.countByEducationalResourceAndLikeStatus(resource, true);
        long sharesCount = userEngagementRepository.countByEducationalResourceAndShareStatus(resource, true);
        long savesCount = userEngagementRepository.countByEducationalResourceAndSavedStatus(resource, true);

        boolean isLikedByUser = userEngagementRepository.existsByUserAndEducationalResourceAndLikeStatus(user, resource, true);
        boolean isSharedByUser = userEngagementRepository.existsByUserAndEducationalResourceAndShareStatus(user, resource, true);
        boolean isSavedByUser = userEngagementRepository.existsByUserAndEducationalResourceAndSavedStatus(user, resource, true);

        return EducationalResourceDTO.builder()
                .resourceId(resource.getId())
                .title(resource.getTitle())
                .content(resource.getContent())
                .contentType(resource.getContentType().getTypeName())
                .isExternalMedia(resource.isExternalMedia())
                .mediaUrl(resource.getMediaUrl())
                .likesCount(likesCount)
                .sharesCount(sharesCount)
                .savesCount(savesCount)
                .createdAt(resource.getCreatedAt())
                .categories(resource.getCategories().stream().map(Category::getCategoryName).collect(Collectors.toSet()))
                .likedByUser(isLikedByUser)
                .sharedByUser(isSharedByUser)
                .savedByUser(isSavedByUser)
                .build();
    }

    /**
     * Updates or creates a user engagement for a specific educational resource.
     *
     * <p>
     * It checks if an engagement already exists for the given user and educational resource.
     * If an existing engagement is found, it toggles the specified engagement status based on the provided parameters.
     * If no engagement is found, a new user engagement is created with the specified engagement statuses.
     * </p>
     *
     * @param userId       The id of the user performing the action.
     * @param resource     The educational resource being engaged with.
     * @param likeStatus   The like status to be toggled.
     * @param shareStatus  The share status to be toggled.
     * @param savedStatus  The saved status to be toggled.
     *
     * @throws ApiException If an error occurs while updating or saving the user engagement.
     */
    private void updateUserEngagement(Long userId, EducationalResource resource, boolean likeStatus, boolean shareStatus, boolean savedStatus) {
        User user = userService.getJpaManagedUser(userId);

        Optional<UserEngagement> existingEngagement = userEngagementRepository.findByUserIdAndEducationalResource(userId, resource);

        // if an existing engagement found for user-resource, toggle the specified statuses
        existingEngagement.ifPresentOrElse(
                engagement -> {
                    engagement.setLikeStatus(toggleStatus(engagement.isLikeStatus(), likeStatus));
                    engagement.setShareStatus(toggleStatus(engagement.isShareStatus(), shareStatus));
                    engagement.setSavedStatus(toggleStatus(engagement.isSavedStatus(), savedStatus));
                    userEngagementRepository.save(engagement);
                },
                // if not, create a new one with the specified statuses
                () -> {
                    UserEngagement newEngagement = UserEngagement.builder()
                            .likeStatus(likeStatus)
                            .shareStatus(shareStatus)
                            .savedStatus(savedStatus)
                            .educationalResource(resource)
                            .user(user)
                            .build();
                    userEngagementRepository.save(newEngagement);
                }
        );
    }

    @Transactional
    public EducationalResource attachImage(EducationalResource resource, MultipartFile image) {
        String resourceImageUrl = setResourceImageUrl(resource.getId());
        saveImage(resource.getId(), image);
        resource.setMediaUrl(resourceImageUrl);

        return resource;
    }

    private void saveImage(Long resourceId, MultipartFile image) {
        Path fileStorageLocation = Paths.get(System.getProperty("user.home") + "/Downloads/images/").toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation)) {
            try {
                Files.createDirectories(fileStorageLocation);
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new ApiException("Unable to create directories to save image");
            }
            log.info("Created directories: {}", fileStorageLocation);
        }

        try {
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(resourceId + ".png"), REPLACE_EXISTING);
        } catch (IOException exception) {
            log.error(exception.getMessage());
            throw new ApiException(exception.getMessage());
        }
        log.info("File saved in: {} folder", fileStorageLocation);
    }

    private String setResourceImageUrl(Long resourceId) {
        return fromCurrentContextPath().path("/eco-learn/resource/image/" + resourceId + ".png").toUriString();
    }

    /**
     * Toggles the status based on the provided parameters.
     *
     * @param currentStatus The current status.
     * @param newStatus     The new status.
     * @return The toggled status.
     */
    private boolean toggleStatus(boolean currentStatus, boolean newStatus) {
        return !currentStatus && newStatus;
    }

    /**
     * Finds a content type by its name.
     *
     * @param contentTypeName The name of the content type.
     * @return The found content type.
     * @throws ApiException If no valid content type is found.
     */
    private ContentType findContentTypeByName(String contentTypeName) {
        return contentTypeRepository.findFirstByTypeName(contentTypeName)
                .orElseThrow(() -> new ApiException("No valid content type found by name: " + contentTypeName));
    }

    /**
     * Finds categories by their names.
     *
     * @param categoryNames The names of the categories.
     * @return The set of found categories.
     * @throws ApiException If no valid categories are found.
     */
    private Set<Category> findCategoriesByNames(String[] categoryNames) {
        return Arrays.stream(categoryNames)
                .map(this::findCategoryByName)
                .collect(Collectors.toSet());
    }

    private Category findCategoryByName(String categoryName) {
        return categoryRepository.findFirstByCategoryName(categoryName.trim())
                .orElseThrow(() -> new ApiException("No valid category found by name: " + categoryName));
    }

    private EducationalResource getResourceById(Long resourceId) {
        return educationalResourceRepository.findById(resourceId)
                .orElseThrow(() -> new ApiException("Educational resource not found with ID: " + resourceId));
    }

}
