package io.rewardsapp.service;

import io.rewardsapp.domain.RecyclingCenter;
import org.springframework.data.domain.Page;


public interface RecyclingCenterService {
    Page<RecyclingCenter> getCenters(Integer page, Integer size);

    Iterable<RecyclingCenter> getCenters();

    RecyclingCenter createCenter(RecyclingCenter newCenter);

    Page<RecyclingCenter> searchCenters(String name, int page, int size);

    Page<RecyclingCenter> searchCenters(
            String name,
            String county,
            String city,
            String materials,
            int page,
            int size,
            String sortBy,
            String sortOrder
    );
}
