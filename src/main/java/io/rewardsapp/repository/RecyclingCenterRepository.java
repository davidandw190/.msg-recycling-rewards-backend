package io.rewardsapp.repository;

import io.rewardsapp.domain.RecyclingCenter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecyclingCenterRepository extends PagingAndSortingRepository<RecyclingCenter, Long>, ListCrudRepository<RecyclingCenter, Long>, JpaSpecificationExecutor<RecyclingCenter> {

    Page<RecyclingCenter> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
