package io.rewardsapp.service.implementation;

import io.rewardsapp.domain.RecyclableMaterial;
import io.rewardsapp.repository.MaterialsRepository;
import io.rewardsapp.service.MaterialsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialsServiceImpl implements MaterialsService {
    private final MaterialsRepository materialsRepository;

    @Override
    public List<RecyclableMaterial> getAllMaterials() {
        return materialsRepository.findAll();
    }

}
