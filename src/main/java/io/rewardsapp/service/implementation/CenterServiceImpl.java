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

/**
 * Service implementation for managing recycling centers.
 */
@Slf4j
@Service
@AllArgsConstructor
public class CenterServiceImpl implements CenterService {

    private final CenterRepository centerRepository;
    private final MaterialsRepository materialsRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Retrieves a paginated list of recycling centers.
     *
     * @param page The page number.
     * @param size The number of items per page.
     * @return A paginated list of recycling centers.
     */
    @Override
    public Page<RecyclingCenter> getCenters(Integer page, Integer size) {
        return centerRepository.findAll(PageRequest.of(page, size));
    }

    /**
     * Retrieves a list of all recycling centers.
     *
     * @return A list of recycling centers.
     */
    @Override
    public Iterable<RecyclingCenter> getCenters() {
        return centerRepository.findAll();
    }

    /**
     * Creates a new recycling center based on the provided form.
     *
     * @param form The form containing information for creating a new center.
     * @return The created recycling center.
     * @throws ApiException If there is an issue creating the recycling center.
     */
    @Override
    public RecyclingCenter createCenter(CreateCenterForm form) {
        checkCenterValidity(form.name(), form.city());

        List<RecyclableMaterial> materials = mapMaterialNamesToEntities(form.materials());

        return centerRepository.save(buildRecyclingCenter(form, materials));
    }

    /**
     * Searches for recycling centers based on the provided name, page, and size.
     *
     * @param name The name to search for.
     * @param page The page number.
     * @param size The number of items per page.
     * @return A paginated list of recycling centers matching the search criteria.
     */
    @Override
    public Page<RecyclingCenter> searchCenters(String name, int page, int size) {
        return centerRepository.findByNameContainingIgnoreCase(name, of(page, size));
    }

    /**
     * Searches for recycling centers based on various criteria, including name, county, city, materials, and sorting options.
     *
     * @param name      The name to search for.
     * @param county    The county to filter by.
     * @param city      The city to filter by.
     * @param materials The list of materials accepted by the centers.
     * @param page      The page number.
     * @param size      The number of items per page.
     * @param sortBy    The field to sort by.
     * @param sortOrder The sorting order (ASC or DESC).
     * @return A paginated list of recycling centers matching the search criteria.
     */
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

    /**
     * Retrieves a recycling center by its ID.
     *
     * @param id The ID of the recycling center.
     * @return The recycling center with the specified ID.
     * @throws ApiException If no center is found by the specified ID.
     */
    @Override
    public RecyclingCenter getCenter(Long id) {
        return centerRepository.findById(id).orElseThrow(() -> new ApiException("No center found."));
    }

    /**
     * Updates an existing recycling center based on the provided form.
     *
     * @param form The form containing information for updating the center.
     * @return The updated recycling center.
     * @throws ApiException If there is an issue updating the recycling center.
     */
    @Override
    public RecyclingCenter updateCenter(UpdateCenterForm form) {

        RecyclingCenter existingCenter = centerRepository.findById(form.centerId()).orElseThrow(
                () -> new ApiException("No center found by specified id.")
        );

        checkCenterValidityExcludeCurrent(form.centerId(), form.name(), form.city());

        List<RecyclableMaterial> materials = mapMaterialNamesToEntities(form.materials());

        buildUpdatedRecyclingCenter(form, existingCenter, materials);

        return centerRepository.save(existingCenter);
    }

    private void checkCenterValidity(String name, String city) {
        if (centerRepository.existsRecyclingCenterByNameAndCity(name, city)) {
            throw new ApiException("A recycling center with the same name already exists in this county.");
        }
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
                .openingHour(LocalTime.parse(form.openingHour(), formatter))
                .closingHour(LocalTime.parse(form.closingHour(), formatter))
                .build();
    }

    private void buildUpdatedRecyclingCenter(UpdateCenterForm form, RecyclingCenter center, List<RecyclableMaterial> materials) {
        center.setCity(form.city());
        center.setCounty(form.county());
        center.setAddress(form.address());
        center.setContact(form.contact());
        center.setName(form.name());
        center.setAlwaysOpen(form.alwaysOpen());
        center.setOpeningHour(LocalTime.parse(form.openingHour(), formatter));
        center.setClosingHour(LocalTime.parse(form.closingHour(), formatter));
        center.setAcceptedMaterials(materials);
    }


}
