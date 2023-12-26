package io.rewardsapp.repository.implementation;

import io.rewardsapp.domain.RecyclingCenter;
import io.rewardsapp.repository.RecyclingCenterRepository;
import io.rewardsapp.service.RecyclingCenterService;
import io.rewardsapp.specs.RecyclingCenterSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.domain.PageRequest.of;

@Service
@AllArgsConstructor
public class RecyclingCenterServiceImpl implements RecyclingCenterService {

    private final RecyclingCenterRepository centerRepository;

    @Override
    public Page<RecyclingCenter> getCenters(Integer page, Integer size) {
        return centerRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Iterable<RecyclingCenter> getCenters() {
        return centerRepository.findAll();
    }

    @Override
    public RecyclingCenter createCenter(RecyclingCenter newCenter) {
        return centerRepository.save(newCenter);
    }

    @Override
    public Page<RecyclingCenter> searchCenters(String name, int page, int size) {
        return centerRepository.findByNameContainingIgnoreCase(name, of(page, size));
    }

    @Override
    public Page<RecyclingCenter> searchCenters(
            String name,
            String county,
            String city,
            List<String> materials,
            int page,
            int size,
            String sortBy,
            String sortOrder
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<RecyclingCenter> specification = RecyclingCenterSpecification.searchCenters(name, county, city, materials);

        return centerRepository.findAll(specification, pageable);
    }
}
