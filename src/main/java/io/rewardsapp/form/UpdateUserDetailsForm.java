package io.rewardsapp.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Represents the form data for user details update.
 */
public record UpdateUserDetailsForm(
        @NotNull(message = "ID field cannot be null or empty") Long id,
        @NotEmpty(message = "First name field cannot be empty") String firstName,
        @NotEmpty(message = "Last name field cannot be empty") String lastName,
        @NotEmpty(message = "Email field cannot be empty") @Email(message = "Invalid email. Please enter a valid email address") String email,
        @NotEmpty(message = "City field cannot be empty") String city,
        @NotEmpty(message = "County field cannot be empty") String county,
        @Pattern(regexp = "^\\d{10}$", message = "Invalid phone number. The number should contain 10 digits.") String phone,
        String address,
        String bio
) {}