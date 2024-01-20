package io.rewardsapp.repository;

import io.rewardsapp.domain.EducationalResource;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationalResourceRepository extends PagingAndSortingRepository<EducationalResource, Long>,
        ListCrudRepository<EducationalResource, Long>,
        JpaSpecificationExecutor<EducationalResource> {
}
