package io.rewardsapp.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Represents the form data for updating user details.
 * Contains fields to specify user information such as first name, last name, email, contact details, and address.
 */
public record UpdateUserDetailsForm(
        @NotNull(message = "ID field cannot be null or empty") Long id,
        @NotEmpty(message = "Please enter the first name.") String firstName,
        @NotEmpty(message = "Please enter the last name.") String lastName,
        @NotEmpty(message = "Please enter a valid email address.")
        @Email(message = "Invalid email format. Please enter a valid email address.") String email,
        @NotEmpty(message = "Please enter the city.") String city,
        @NotEmpty(message = "Please enter the county.") String county,
        @Pattern(regexp = "^\\d{10}$", message = "Invalid phone number. The number should contain 10 digits.") String phone,

        String address,
        String bio
) {}