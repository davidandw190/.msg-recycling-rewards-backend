package io.rewardsapp.repository;

import io.rewardsapp.domain.ContentType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentTypeRepository extends PagingAndSortingRepository<ContentType, Long>,
        ListCrudRepository<ContentType, Long>,
        JpaSpecificationExecutor<ContentType> {
}

