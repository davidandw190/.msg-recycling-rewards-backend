package io.rewardsapp.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@SuperBuilder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "materials")
public class RecycledMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_id")
    private Long materialId;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "recycledMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRecyclingActivity> recyclingActivities;
}
