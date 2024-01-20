package io.rewardsapp.service.implementation;

import io.rewardsapp.domain.recycling.RecyclableMaterial;
import io.rewardsapp.domain.recycling.RecyclingCenter;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.form.CreateCenterForm;
import io.rewardsapp.form.UpdateCenterForm;
import io.rewardsapp.repository.CenterRepository;
import io.rewardsapp.repository.MaterialsRepository;
import io.rewardsapp.service.CenterService;
import io.rewardsapp.specs.RecyclingCenterSpecification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.domain.PageRequest.of;

@Slf4j
@Service
@AllArgsConstructor
public class CenterServiceImpl implements CenterService {

    private final CenterRepository centerRepository;
    private final MaterialsRepository materialsRepository;

    @Override
    public Page<RecyclingCenter> getCenters(Integer page, Integer size) {
        return centerRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Iterable<RecyclingCenter> getCenters() {
        return centerRepository.findAll();
    }

    @Override
    public RecyclingCenter createCenter(CreateCenterForm form) {
//        validateForm(form);
        checkCenterValidity(form.name(), form.city());

        List<RecyclableMaterial> materials = mapMaterialNamesToEntities(form.materials());

        return centerRepository.save(buildRecyclingCenter(form, materials));
    }

    private void checkCenterValidity(String name, String city) {
        if (centerRepository.existsRecyclingCenterByNameAndCity(name, city)) {
            throw new ApiException("Center name already exists");
        }
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

    @Override
    public RecyclingCenter getCenter(Long id) {
        return centerRepository.findById(id).orElseThrow(() -> new RuntimeException("No center found."));
    }

    @Override
    public RecyclingCenter updateCenter(UpdateCenterForm form) {
//        checkCenterValidityExcludeCurrent(form.centerId(), form.name(), form.city());

        RecyclingCenter existingCenter = centerRepository.findById(form.centerId()).orElseThrow(
                () -> new ApiException("No center found by specified id.")
        );

        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> AFTER EXISTS CHECK");

        List<RecyclableMaterial> materials = mapMaterialNamesToEntities(form.materials());


        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> AFTER MAP");

        buildUpdatedRecyclingCenter(form, existingCenter, materials);

        return centerRepository.save(existingCenter);
    }

    private void checkCenterValidityExcludeCurrent(Long currentCenterId, String name, String city) {
        if (centerRepository.existsRecyclingCenterByNameAndCityAndCenterIdNot(name, city, currentCenterId)) {
            throw new ApiException("A recycling center with the same name already exists in this county.");
        }
    }

    private List<RecyclableMaterial> mapMaterialNamesToEntities(String[] materialNames) {
        List<RecyclableMaterial> materials = new ArrayList<>(materialNames.length);

        for (String materialName : materialNames) {
            RecyclableMaterial material = materialsRepository.findFirstByName(materialName)
                    .orElseThrow(() -> new ApiException("Material not found: " + materialName));

            materials.add(material);
        }
        return materials;
    }

    private RecyclingCenter buildRecyclingCenter(CreateCenterForm form, List<RecyclableMaterial> materials) {
        return RecyclingCenter.builder()
                .name(form.name())
                .contact(form.contact())
                .county(form.county())
                .city(form.city())
                .address(form.address())
                .acceptedMaterials(materials)
                .alwaysOpen(form.alwaysOpen())
                .openingHour(parseLocalTime(form.openingHour()))
                .closingHour(parseLocalTime(form.closingHour()))
                .build();
    }

    private void buildUpdatedRecyclingCenter(UpdateCenterForm form, RecyclingCenter center, List<RecyclableMaterial> materials) {

        center.setCity(form.city());
        center.setCounty(form.county());
        center.setAddress(form.address());
        center.setContact(form.contact());
        center.setName(form.name());
        center.setAlwaysOpen(form.alwaysOpen());
        center.setAcceptedMaterials(materials);
    }

    private LocalTime parseLocalTime(String time) {
        return (time != null) ? LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm")) : LocalTime.parse("00:00", DateTimeFormatter.ofPattern("HH:mm"));
    }
}
