package io.rewardsapp.specs;

import io.rewardsapp.domain.RecyclingCenter;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification for querying RecyclingCenters based on search criteria.
 */
@Component
public class RecyclingCenterSpecification {

    /**
     * Generates a Specification for searching RecyclingCenters.
     *
     * @param name      The name to search for.
     * @param county    The county to search for.
     * @param city      The city to search for.
     * @param materials The list of materials accepted by the recycling center.
     * @return A Specification for querying RecyclingCenters.
     */
    public static Specification<RecyclingCenter> searchCenters(
            String name,
            String county,
            String city,
            List<String> materials
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(name)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if (StringUtils.hasText(county)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("county")), "%" + county.toLowerCase() + "%"));
            }

            if (StringUtils.hasText(city)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("city")), "%" + city.toLowerCase() + "%"));
            }

            // Subquery for materials to avoid unnecessary joins
            if (!materials.isEmpty()) {
                List<Predicate> materialPredicates = new ArrayList<>();
                for (String material : materials) {
                    materialPredicates.add(criteriaBuilder.isMember(material, root.get("acceptedMaterials")));
                }
                predicates.add(criteriaBuilder.and(materialPredicates.toArray(new Predicate[0])));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
