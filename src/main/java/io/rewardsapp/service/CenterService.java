package io.rewardsapp.service;

import io.rewardsapp.domain.recycling.RecyclingCenter;
import io.rewardsapp.form.CreateCenterForm;
import io.rewardsapp.form.UpdateCenterForm;
import org.springframework.data.domain.Page;

import java.util.List;


public interface CenterService {
    Page<RecyclingCenter> getCenters(Integer page, Integer size);

    Iterable<RecyclingCenter> getCenters();

    RecyclingCenter createCenter(CreateCenterForm form);

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

    RecyclingCenter updateCenter(UpdateCenterForm form);
}
