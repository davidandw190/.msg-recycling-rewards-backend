package io.rewardsapp.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO representing an educational resource with engagement details and user-specific engagement status.
 */
@Builder
public record EducationalResourceDTO(
    Long resourceId,
    String title,
    String content,
    String contentType,
    boolean isExternalMedia,
    String mediaUrl,
    long likesCount,
    long sharesCount,
    long savesCount,
    LocalDateTime createdAt,
    Set<String> categories,
    boolean likedByUser,
    boolean sharedByUser,
    boolean savedByUser
) { }