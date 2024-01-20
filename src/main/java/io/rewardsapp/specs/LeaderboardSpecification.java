package io.rewardsapp.specs;

import io.rewardsapp.domain.auth.User;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification class for querying users based on criteria for leaderboard generation.
 */
public class LeaderboardSpecification {

    /**
     * Constructs a specification to filter users based on the specified county and exclude those with administrative roles.
     *
     * @param county    The county for which users are to be filtered.
     * @param sortBy    The field by which the results should be sorted.
     * @param sortOrder The order in which the results should be sorted (asc or desc).
     * @return The specification for querying users.
     */
    public static Specification<User> getResults(String county, String sortBy, String sortOrder) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(county)) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("county")), county.toLowerCase()));
            }

            // TODO exclude users with roles ADMIN or SYSADMIN

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Constructs a specification to order the results based on the specified sorting criteria.
     *
     * @param sortBy    The field by which the results should be sorted.
     * @param sortOrder The order in which the results should be sorted (asc or desc).
     * @return A Specification for ordering users.
     */
    public static Specification<User> orderBy(String sortBy, String sortOrder) {
        return (root, query, criteriaBuilder) -> {
            if ("asc".equalsIgnoreCase(sortOrder)) {
                query.orderBy(criteriaBuilder.asc(root.get(sortBy)));
            } else {
                query.orderBy(criteriaBuilder.desc(root.get(sortBy)));
            }
            return criteriaBuilder.and();
        };
    }

    /**
     * Combines the filtering and ordering specifications to build the final specification for leaderboard queries.
     *
     * @param county    The county for which users are to be filtered.
     * @param sortBy    The field by which the results should be sorted.
     * @param sortOrder The order in which the results should be sorted (asc or desc).
     * @return The final specification for querying users for leaderboards.
     */
    public static Specification<User> buildLeaderboardSpecification(String county, String sortBy, String sortOrder) {
        return Specification.where(getResults(county, sortBy, sortOrder))
                .and(orderBy(sortBy, sortOrder));
    }
}
