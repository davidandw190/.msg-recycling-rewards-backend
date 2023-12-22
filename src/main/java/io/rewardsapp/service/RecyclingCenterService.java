package io.rewardsapp.service;

import io.rewardsapp.domain.RecyclingCenter;
import org.springframework.data.domain.Page;


public interface RecyclingCenterService {
    Page<RecyclingCenter> getRecyclingCenters(Integer page, Integer size);

    Iterable<RecyclingCenter> getRecyclingCenters();

    RecyclingCenter createRecyclingCenter(RecyclingCenter newCenter);
}
