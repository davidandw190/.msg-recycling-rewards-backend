package io.rewardsapp.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_saved_resources")
public class UserSavedResource {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "resource_id", nullable = false)
    @JsonBackReference
    private EducationalResource educationalResource;
}
