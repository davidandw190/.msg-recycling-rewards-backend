package io.rewardsapp.service;

import io.rewardsapp.domain.educational.EducationalResource;
import jakarta.transaction.Transactional;

public interface EducationalResourcesService {
    @Transactional
    EducationalResource createEducationalResource(String title, String content, String contentType, String[] categories);
}