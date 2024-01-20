package io.rewardsapp.repository;

import io.rewardsapp.domain.Category;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends PagingAndSortingRepository<Category, Long>,
        ListCrudRepository<Category, Long>,
        JpaSpecificationExecutor<Category> {
}
