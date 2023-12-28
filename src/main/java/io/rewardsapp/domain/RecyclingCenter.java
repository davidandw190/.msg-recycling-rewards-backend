package io.rewardsapp.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;

@Entity
@SuperBuilder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recycling_centers")
public class RecyclingCenter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "center_id")
    private Long centerId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "contact")
    private String contact;

    @Column(name = "county", nullable = false)
    private String county;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "opening_hour", columnDefinition = "TIME")
    private LocalTime openingHour;

    @Column(name = "closing_hour", columnDefinition = "TIME")
    private LocalTime closingHour;

    @Column(name = "always_open", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean alwaysOpen;


    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "materials_to_recycle",
            joinColumns = @JoinColumn(name = "center_id"),
            inverseJoinColumns = @JoinColumn(name = "material_id"))
    private Collection<RecyclableMaterial> acceptedMaterials;

    @JsonIgnore
    @OneToMany(mappedBy = "recyclingCenter", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<UserRecyclingActivity> recyclingActivities;
}
