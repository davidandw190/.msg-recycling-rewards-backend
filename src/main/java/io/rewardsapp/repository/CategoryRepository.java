package io.rewardsapp.repository;

import io.rewardsapp.domain.educational.Category;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends PagingAndSortingRepository<Category, Long>,
        ListCrudRepository<Category, Long>,
        JpaSpecificationExecutor<Category> {

    Optional<Category> findFirstByCategoryName(String categoryName);

    @Query("SELECT c.categoryName FROM Category c")
    List<String> getAllCategoryNames();
}
