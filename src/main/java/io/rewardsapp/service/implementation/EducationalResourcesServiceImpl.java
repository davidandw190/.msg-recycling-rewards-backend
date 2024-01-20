package io.rewardsapp.service.implementation;

import io.rewardsapp.domain.auth.User;
import io.rewardsapp.domain.educational.Category;
import io.rewardsapp.domain.educational.ContentType;
import io.rewardsapp.domain.educational.EducationalResource;
import io.rewardsapp.domain.educational.UserEngagement;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.repository.CategoryRepository;
import io.rewardsapp.repository.ContentTypeRepository;
import io.rewardsapp.repository.EducationalResourceRepository;
import io.rewardsapp.repository.UserEngagementRepository;
import io.rewardsapp.service.EducationalResourcesService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EducationalResourcesServiceImpl implements EducationalResourcesService {

    private final EducationalResourceRepository educationalResourceRepository;
    private final UserEngagementRepository userEngagementRepository;
    private final ContentTypeRepository contentTypeRepository;
    private final CategoryRepository categoryRepository;


    @Transactional
    @Override
    public EducationalResource createEducationalResource(String title, String content, String contentType, String[] categories) {

        ContentType chosenContentType = contentTypeRepository.findFirstByTypeName(contentType)
                .orElseThrow(() -> new ApiException("No valid content type found by name: " + contentType));

        List<Category> chosenCategories = getCategoriesByNames(new HashSet<>(List.of(categories)));

        EducationalResource educationalResource = EducationalResource.builder()
                .title(title)
                .content(content)
                .contentType(chosenContentType)
                .likesCount(0L)
                .createdAt(LocalDateTime.now())
                .categories(chosenCategories)
                .build();

        return educationalResourceRepository.save(educationalResource);
    }

    @Transactional
    @Override
    public void likeResource(User user, Long resourceId) {
        try {
            EducationalResource resource = getResourceById(resourceId);

            Optional<UserEngagement> existingEngagement = userEngagementRepository.findByUserAndEducationalResource(user, resource);
            if (existingEngagement.isPresent()) {
                existingEngagement.get().setLikeStatus(true);
                userEngagementRepository.save(existingEngagement.get());

            } else {
                userEngagementRepository.save(new UserEngagement(user, resource, true, false));
            }

            log.info("User with ID {} liked educational resource with ID {}", user.getId(), resourceId);

        } catch (Exception e) {
            log.error("Error occurred while liking resource", e);
            throw new ApiException("Error occurred while liking resource");
        }
    }

    private List<Category> getCategoriesByNames(Set<String> categoryNames) {
        List<Category> chosenCategories = new ArrayList<>(categoryNames.size());

        for (String categoryName : categoryNames) {
            chosenCategories.add(
                    categoryRepository.findFirstByCategoryName(categoryName)
                            .orElseThrow( () -> new ApiException("No valid category type found by name: " + categoryName)));
        }

        if (chosenCategories.size() != categoryNames.size()) {
            throw new ApiException("Invalid category Names");
        }

        return chosenCategories;
    }

    private EducationalResource getResourceById(Long resourceId) {
        return educationalResourceRepository.findById(resourceId)
                .orElseThrow(() -> new ApiException("Educational resource not found"));
    }

}
