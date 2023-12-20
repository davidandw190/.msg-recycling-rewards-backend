package io.rewardsapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

/**
 * The User class represents a user in the recycling rewards application.
 */
@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@JsonInclude(NON_DEFAULT)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "first_name", nullable = false)
    @NotEmpty(message = "First name field cannot be empty")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotEmpty(message = "Last name field cannot be empty")
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    @NotEmpty(message = "Email field cannot be empty")
    @Email(message = "Invalid email. Please provide a valid email address")
    private String email;

    @Column(name = "password", nullable = false)
    @NotEmpty(message  = "Password field cannot be empty")
    private String password;

    @Column(name = "county", nullable = false)
    @NotEmpty(message = "County field cannot be empty")
    private String county;

    @Column(name = "city", nullable = false)
    @NotEmpty(message = "City field cannot be empty")
    private String city;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "notif_enabled", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean notificationsEnabled;

    @Column(name = "enabled", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean enabled;

    @Column(name = "non_locked", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean notLocked;

    @Column(name = "using_mfa", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean usingMfa;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<UserRecyclingActivity> recyclingActivities;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private RewardPoints rewardPoints;

}