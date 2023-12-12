package io.rewardsapp.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

/**
 * The User class represents a user in the recycling rewards application.
 * It includes information such as user details, authentication status, and creation timestamp.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_DEFAULT)
public class User {
    private Long id;

    @NotEmpty(message = "First name field cannot be empty")
    private String firstName;

    @NotEmpty(message = "Last name field cannot be empty")
    private String lastName;

    @NotEmpty(message = "Email field cannot be empty")
    @Email(message = "Invalid email. Please provide a valid email address")
    private String email;

    @NotEmpty(message = "Password field cannot be empty")
    private String password;

    @NotEmpty(message = "City field cannot be empty")
    private String city;

    private String address;
    private String phone;
    private String bio;
    private String imageUrl;

    private boolean notificationsEnabled;
    private boolean enabled;
    private boolean notLocked;
    private boolean usingMfa;

    private LocalDateTime createdAt;
}