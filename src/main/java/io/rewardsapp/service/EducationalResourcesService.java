package io.rewardsapp.service;

import io.rewardsapp.domain.auth.User;
import io.rewardsapp.domain.educational.EducationalResource;
import io.rewardsapp.dto.EducationalResourceDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EducationalResourcesService {
    @Transactional
    EducationalResource createEducationalResource(String title, String content, String contentType, String[] categories);

    @Transactional
    Page<EducationalResourceDTO> searchResources(
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
    );

    @Transactional
    void likeResource(Long userId, Long resourceId);

    @Transactional
    void saveResource(Long userId, Long resourceId);

    @Transactional
    void shareResource(Long userId, Long resourceId);
}