package io.rewardsapp.specs;

import io.rewardsapp.domain.RecyclableMaterial;
import io.rewardsapp.domain.RecyclingCenter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class RecyclingCenterSpecification {

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

            if (!materials.isEmpty()) {
                Join<RecyclingCenter, RecyclableMaterial> materialsJoin = root.join("acceptedMaterials", JoinType.LEFT);
                CriteriaBuilder.In<String> inClause = criteriaBuilder.in(materialsJoin.get("name"));
                materials.forEach(inClause::value);
                predicates.add(inClause);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}