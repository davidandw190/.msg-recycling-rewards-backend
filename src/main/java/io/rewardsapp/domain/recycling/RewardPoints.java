package io.rewardsapp.domain.recycling;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.rewardsapp.domain.auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@SuperBuilder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reward_points")
public class RewardPoints {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(name = "total_points", nullable = false)
    private Long totalPoints;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

}
