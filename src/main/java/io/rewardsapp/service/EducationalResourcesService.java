package io.rewardsapp.service;

import io.rewardsapp.domain.auth.User;
import io.rewardsapp.domain.educational.EducationalResource;
import jakarta.transaction.Transactional;

public interface EducationalResourcesService {
    @Transactional
    EducationalResource createEducationalResource(String title, String content, String contentType, String[] categories);

    @Transactional
    void likeResource(User user, Long resourceId);

    @Transactional
    void saveResource(User user, Long resourceId);

    @Transactional
    void shareResource(User user, Long resourceId);
}