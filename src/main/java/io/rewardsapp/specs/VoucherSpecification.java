package io.rewardsapp.specs;

import io.rewardsapp.domain.Voucher;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class VoucherSpecification {

    public static Specification<Voucher> searchVouchers(
            Long userId,
            String uniqueCode,
            Boolean redeemed,
            Boolean expired
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userId != null) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));
            }

            if (StringUtils.hasText(uniqueCode)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("uniqueCode")), "%" + uniqueCode.toLowerCase() + "%"));
            }

            if (redeemed != null) {
                predicates.add(criteriaBuilder.equal(root.get("redeemed"), redeemed));
            }

            if (expired != null) {
                LocalDateTime currentDateTime = LocalDateTime.now();
                if (expired) {
                    predicates.add(criteriaBuilder.lessThan(root.get("expiresAt"), currentDateTime));
                } else {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("expiresAt"), currentDateTime));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
