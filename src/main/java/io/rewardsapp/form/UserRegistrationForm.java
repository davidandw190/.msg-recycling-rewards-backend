package io.rewardsapp.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record UserRegistrationForm(
        @NotEmpty(message = "Please enter your first name.") String firstName,
        @NotEmpty(message = "Please enter your last name.") String lastName,
        @NotEmpty(message = "Please enter your email.")
        @Email(message = "Invalid email. Please enter a valid email address") String email,
        @NotEmpty(message = "Please enter your county.") String county,
        @NotEmpty(message = "Please enter your city.") String city,
        @NotEmpty(message = "Please enter your password.") String password,
        @NotEmpty(message = "Please confirm your password.") String confirmPassword
) {
}
