package io.rewardsapp.domain.educational;

import io.rewardsapp.domain.auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UserEngagementId.class)
@Table(name = "user_engagement")
public class UserEngagement {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "resource_id", nullable = false)
    private EducationalResource educationalResource;

    @Column(name = "like_status", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean likeStatus;

    @Column(name = "share_status", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean shareStatus;

    @Column(name = "saved_status", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean savedStatus;
}

