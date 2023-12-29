package io.rewardsapp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

@Entity
@SuperBuilder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "materials")
public class RecyclableMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_id")
    private Long materialId;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "reward_points", nullable = false)
    private String rewardPoints;

    @OneToMany(mappedBy = "recycledMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference("recycledMaterial-activities")
    private Collection<UserRecyclingActivity> recyclingActivities;

    @ManyToMany(mappedBy = "acceptedMaterials")
    @JsonBackReference("recyclingCenter-materials")
    private Collection<RecyclingCenter> recyclingCenters;
}
