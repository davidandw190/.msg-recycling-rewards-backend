package io.rewardsapp.service;

import io.rewardsapp.domain.EducationalResource;
import jakarta.transaction.Transactional;

import java.util.List;

public interface EducationalResourcesService {
    @Transactional
    EducationalResource createEducationalResource(String title, String content, Long contentTypeId,
                                                  List<Long> categoryIds);
}
