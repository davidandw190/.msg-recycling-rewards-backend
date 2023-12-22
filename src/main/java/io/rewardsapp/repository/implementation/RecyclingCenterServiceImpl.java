package io.rewardsapp.repository.implementation;

import io.rewardsapp.domain.RecyclingCenter;
import io.rewardsapp.repository.RecyclingCenterRepository;
import io.rewardsapp.service.RecyclingCenterService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RecyclingCenterServiceImpl implements RecyclingCenterService {

    private final RecyclingCenterRepository centerRepository;

    @Override
    public Page<RecyclingCenter> getRecyclingCenters(Integer page, Integer size) {
        return centerRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Iterable<RecyclingCenter> getRecyclingCenters() {
        return centerRepository.findAll();
    }

    @Override
    public RecyclingCenter createRecyclingCenter(RecyclingCenter newCenter) {
        return centerRepository.save(newCenter);
    }
}
