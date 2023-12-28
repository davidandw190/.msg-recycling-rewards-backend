package io.rewardsapp.specs;

import io.rewardsapp.domain.UserRecyclingActivity;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserRecyclingActivitySpecification {
    /**
     * Creates a specification to fetch user recycling activities statistics for the last month.
     *
     * @param userId    The ID of the user.
     * @param lastMonth The LocalDateTime representing the start of the last month.
     * @return The Specification object.
     */
    public static Specification<UserRecyclingActivity> userStatsForLastMonth(Long userId, LocalDateTime lastMonth) {
        return (root, query, criteriaBuilder) -> {
            root.fetch("recycledMaterial", JoinType.LEFT);
            root.fetch("user", JoinType.LEFT);

            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("user").get("id"), userId),
                    criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), lastMonth)
            );
        };
    }
}
