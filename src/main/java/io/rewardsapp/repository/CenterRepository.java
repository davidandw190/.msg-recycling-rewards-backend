package io.rewardsapp.repository;

import io.rewardsapp.domain.recycling.RecyclingCenter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CenterRepository extends
        PagingAndSortingRepository<RecyclingCenter, Long>,
        ListCrudRepository<RecyclingCenter, Long>,
        JpaSpecificationExecutor<RecyclingCenter> {

    Page<RecyclingCenter> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsRecyclingCenterByNameAndCity(String name, String city);

    boolean existsRecyclingCenterByNameAndCityAndCenterIdNot(String name, String city, Long centerId);
}
