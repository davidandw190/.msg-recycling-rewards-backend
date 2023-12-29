package io.rewardsapp.service;

import io.rewardsapp.domain.RecyclingCenter;
import io.rewardsapp.dto.UserDTO;
import org.springframework.data.domain.Page;

import java.util.List;


public interface CenterService {
    Page<RecyclingCenter> getCenters(Integer page, Integer size);

    Iterable<RecyclingCenter> getCenters();

    RecyclingCenter createCenter(RecyclingCenter newCenter);

    Page<RecyclingCenter> searchCenters(String name, int page, int size);

    Page<RecyclingCenter> searchCenters(
            String name,
            String county,
            String city,
            List<String> materials,
            int page,
            int size,
            String sortBy,
            String sortOrder
    );

    RecyclingCenter getCenter(Long id);

    RecyclingCenter updateCenter(Long centerId);
}
