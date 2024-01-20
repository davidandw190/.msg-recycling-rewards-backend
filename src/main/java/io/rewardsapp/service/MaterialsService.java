package io.rewardsapp.service;

import io.rewardsapp.domain.recycling.RecyclableMaterial;

import java.util.List;

public interface MaterialsService {
    List<RecyclableMaterial> getAllMaterials();
}
