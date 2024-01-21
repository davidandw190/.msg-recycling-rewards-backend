package io.rewardsapp.specs;

import io.rewardsapp.domain.educational.Category;
import io.rewardsapp.domain.educational.ContentType;
import io.rewardsapp.domain.educational.EducationalResource;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class EducationalResourceSpecification {

    public static Specification<EducationalResource> searchResources(String title, String contentType, List<String> categories, boolean likedOnly, boolean savedOnly) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (title != null && !title.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (contentType != null && !contentType.isEmpty()) {
                Join<EducationalResource, ContentType> contentTypeJoin = root.join("contentType", JoinType.INNER);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(criteriaBuilder.lower(contentTypeJoin.get("typeName")), contentType.toLowerCase()));
            }

            if (categories != null && !categories.isEmpty()) {
                Join<EducationalResource, Category> categoryJoin = root.join("categories", JoinType.INNER);
                predicate = criteriaBuilder.and(predicate, categoryJoin.get("categoryName").in(uppercaseList(categories)));
            }

            if (likedOnly) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.isTrue(root.join("userEngagements", JoinType.LEFT).get("likeStatus")));
            }

            if (savedOnly) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.isTrue(root.join("userEngagements", JoinType.LEFT).get("savedStatus")));
            }

            return predicate;
        };
    }

    private static List<String> uppercaseList(List<String> inputList) {
        return inputList.stream()
                .map(String::toUpperCase)
                .toList();
    }
}
