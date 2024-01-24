package io.rewardsapp.service;

import io.rewardsapp.domain.auth.User;
import io.rewardsapp.domain.educational.EducationalResource;
import io.rewardsapp.dto.EducationalResourceDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EducationalResourcesService {
    @Transactional
    void createEducationalResource(String title, String content, String contentTypeName, String[] categoryNames, MultipartFile file);

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