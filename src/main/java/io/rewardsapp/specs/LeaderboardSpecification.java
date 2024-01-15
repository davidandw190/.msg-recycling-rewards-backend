package io.rewardsapp.specs;

import io.rewardsapp.domain.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardSpecification {

    public static Specification<User> getResults(String county, String sortBy, String sortOrder) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(county)) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("county")), county.toLowerCase()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

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

    public static Specification<User> buildLeaderboardSpecification(String county, String sortBy, String sortOrder) {
        return Specification.where(getResults(county, sortBy, sortOrder))
                .and(orderBy(sortBy, sortOrder));
    }
}
