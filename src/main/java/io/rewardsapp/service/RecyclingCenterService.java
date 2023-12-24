package io.rewardsapp.service;

import io.rewardsapp.domain.RecyclingCenter;
import io.rewardsapp.dto.UserDTO;
import org.springframework.data.domain.Page;


public interface RecyclingCenterService {
    Page<RecyclingCenter> getRecyclingCenters(Integer page, Integer size);

    Iterable<RecyclingCenter> getRecyclingCenters();

    RecyclingCenter createRecyclingCenter(RecyclingCenter newCenter);

    Page<RecyclingCenter> searchCenters(String name, int page, int size);
}
