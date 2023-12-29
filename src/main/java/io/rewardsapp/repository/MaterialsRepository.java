package io.rewardsapp.repository;

import io.rewardsapp.domain.RecyclableMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MaterialsRepository extends JpaRepository<RecyclableMaterial, Long> {

    Optional<RecyclableMaterial> findFirstByName(String name);

}
