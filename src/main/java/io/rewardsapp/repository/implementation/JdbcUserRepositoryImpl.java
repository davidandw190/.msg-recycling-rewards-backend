package io.rewardsapp.repository.implementation;

import io.rewardsapp.domain.Role;
import io.rewardsapp.domain.User;
import io.rewardsapp.domain.UserPrincipal;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.enums.VerificationType;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.form.UpdateUserDetailsForm;
import io.rewardsapp.repository.JdbcUserRepository;
import io.rewardsapp.repository.RoleRepository;
import io.rewardsapp.rowmapper.UserRowMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import static io.rewardsapp.enums.RoleType.ROLE_USER;
import static io.rewardsapp.enums.VerificationType.ACCOUNT;
import static io.rewardsapp.enums.VerificationType.PASSWORD;
import static io.rewardsapp.query.UserQuery.*;
import static io.rewardsapp.utils.SmsUtils.sendSMS;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Map.of;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.time.DateFormatUtils.format;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.hibernate.type.descriptor.java.JdbcDateJavaType.DATE_FORMAT;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

/**
 * Implementation of JdbcUserRepository and UserDetailsService for handling User-related database operations.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcUserRepositoryImpl implements JdbcUserRepository<User>, UserDetailsService {

    private final NamedParameterJdbcTemplate jdbc;
    private final RoleRepository<Role> roleRepository;
    private final BCryptPasswordEncoder encoder;

    /**
     * Creates a new user in the database. The user is disabled by default.
     *
     * @param user The user data to be created.
     * @return The created user.
     * @throws ApiException If there is an issue creating the user.
     */
    @Override
    @Transactional
    public User create(User user) {
        if (getEmailCount(user.getEmail().trim().toLowerCase()) > 0) {
            throw new ApiException("Email already in use. Please use a different email and try again.");
        }
        try {
            KeyHolder holder = new GeneratedKeyHolder();
            user.setEnabled(true);
            user.setCreatedAt(LocalDateTime.now());
            SqlParameterSource parameters = getSqlParameterSource(user);
            jdbc.update(INSERT_USER_QUERY, parameters, holder, new String[]{"user_id"});  // Specify the key column
            log.info("inserted");
            Number key = holder.getKey();
            if (key == null) {
                throw new ApiException("Failed to retrieve the generated key for the user.");
            }
            user.setId(key.longValue());
            roleRepository.addRoleToUser(user.getId(), ROLE_USER.name());
            log.info("added role");
            user.setEnabled(true);
            user.setNotLocked(true);
            log.info("returning..");
            return user;

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    /**
     * Retrieves a list of users from the database based on pagination parameters.
     *
     * @param page     The page number.
     * @param pageSize The number of users per page.
     * @return The list of users for the specified page and size.
     */
    @Override
    public Collection<User> list(int page, int pageSize) {
        return null;
    }

    /**
     * Retrieves a user by their ID from the database.
     *
     * @param userId The ID of the user to retrieve.
     * @return The user object.
     * @throws ApiException If no user is found with the specified ID.
     */
    @Override
    public User get(Long userId) {
        try {
            return jdbc.queryForObject(SELECT_USER_BY_ID_QUERY, Map.of("userId", userId), new UserRowMapper());

        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No User found by id: " + userId);

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public User update(User data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    /**
     * Loads a user by their email address for Spring Security authentication.
     *
     * @param email The email address of the user.
     * @return The UserDetails object for the authenticated user.
     * @throws UsernameNotFoundException If no user is found with the specified email.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User existingUser = getUserByEmail(email);
        if (existingUser == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("User found in database: {}", email);
            return new UserPrincipal(existingUser, roleRepository.getRoleByUserId(existingUser.getId()));

        }
    }

    /**
     * Retrieves a user by their email address from the database.
     *
     * @param email The email address of the user.
     * @return The user object.
     * @throws ApiException If no user is found with the specified email.
     */
    @Override
    public User getUserByEmail(String email) {
        try {
            SqlParameterSource params = new MapSqlParameterSource("email", email);
            String query = "Executing SQL query: " + SELECT_USER_BY_EMAIL_QUERY + " with parameters: " + params;
            log.info(query);
            return jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, params, new UserRowMapper());

        } catch (EmptyResultDataAccessException exception) {
            log.error("No user found by email: {}", email);
            throw new ApiException("No user found by email: " + email);

        } catch (Exception exception) {
            log.error("An error occurred while fetching user by email: {}", email, exception);
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    /**
     * Sends an account verification code to the user's phone number. This method generates
     * a random verification code, associates it with the user in the database, and sends
     * the code to the user's phone via SMS. The verification code is used for account-related
     * activities, such as registration or password reset.
     *
     * @param user The user DTO containing necessary information.
     * @throws ApiException If there is an issue sending the verification code.
     */
    @Override
    public void sendAccountVerificationCode(UserDTO user) {
        String expirationDate = format(addDays(new Date(), 1), DATE_FORMAT);
        String verificationCode = randomAlphabetic(8).toUpperCase();
        try {
            jdbc.update(DELETE_VERIFICATION_CODE_BY_USER_ID, Map.of("userId", user.id()));
            jdbc.update(INSERT_VERIFICATION_CODE_QUERY, Map.of("userId", user.id(), "code", verificationCode, "expirationDate", expirationDate));
            sendSMS(user.phone(), "From: .MsgRecyclingRewards \nVerification code\n" + verificationCode);
            log.info("Verification Code: {}", verificationCode);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }

    }

    /**
     * Creates an account verification code and stores it in the database. This method
     * generates a unique verification URL using a random UUID and the ACCOUNT verification
     * type. The generated URL is associated with the user in the database for account
     * verification purposes.
     *
     * @param user  The user for whom the verification code is created.
     * @return      The verification url
     */
    @Override
    public String createEnableAccountUrl(User user) {
        String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType());
        jdbc.update(INSERT_VERIFICATION_QUERY, Map.of("userId", user.getId(), "url", verificationUrl));
        return verificationUrl;
    }

    /**
     * Updates the details of a user by taking an UpdateUserDetailsForm as input and updating the
     * user's details in the database. It uses the provided form to construct and execute an
     * SQL update query.
     *
     * @param updateUserDetailsForm The form containing updated user details.
     * @return The updated User object.
     * @throws ApiException If there is an issue updating user details in the database.
     */
    @Override
    public User updateUserDetails(UpdateUserDetailsForm updateUserDetailsForm) {
        try {
            jdbc.update(UPDATE_USER_DETAILS_QUERY, getUserDetailsSqlParameterSource(updateUserDetailsForm));
            return get(updateUserDetailsForm.id());

        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No User found by ID: " + updateUserDetailsForm.id());

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    /**
     * Verifies a verification code and retrieves the associated user by checking
     * if the provided verification code has expired. If not, it retrieves the user associated
     * with the code from the database and ensures the code matches the user's email.
     *
     * @param email The email address of the user.
     * @param code  The verification code to be checked.
     * @return The verified user object.
     * @throws ApiException If the verification code is invalid, expired, or an error occurs.
     */
    @Override
    public User verifyCode(String email, String code) {
        if (isVerificationCodeExpired(code)) throw new ApiException("This code has expired. Please login again.");

        try {
            User userByCode = jdbc.queryForObject(SELECT_USER_BY_USER_CODE_QUERY, Map.of("code", code), new UserRowMapper());
            User userByEmail = jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, Map.of("email", email), new UserRowMapper());

            if (Objects.requireNonNull(userByCode).getEmail().equalsIgnoreCase(Objects.requireNonNull(userByEmail).getEmail())) {
                jdbc.update(DELETE_CODE, Map.of("code", code));
                return userByCode;
            } else {
                throw new ApiException("Code is invalid. Please try again.");
            }

        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("Could not find record");

        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    /**
     * Sends a reset password link to the user's email address. It generates a verification URL,
     * associates it with the user in the database, and sends the link to the user's email.
     * The link is used to reset the user's password.
     *
     * @param email The email address of the user.
     * @throws ApiException If there is an issue sending the reset link.
     */
    @Override
    public void resetForgottenPassword(String email) {
        if (getEmailCount(email.trim().toLowerCase()) <= 0) throw new ApiException("There is no account for this email address");

        try {
            String expirationDate = format(addDays(new Date(), 1), DATE_FORMAT);
            User user = getUserByEmail(email);
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), PASSWORD.getType());
            jdbc.update(DELETE_PASSWORD_VERIFICATION_BY_USER_ID_QUERY, Map.of("userId",  user.getId()));
            jdbc.update(INSERT_PASSWORD_VERIFICATION_QUERY, Map.of("userId",  user.getId(), "url", verificationUrl, "expirationDate", expirationDate));
//            sendEmail(user.getFirstName(), email, verificationUrl, PASSWORD);
            log.info("Verification URL: {}", verificationUrl);

        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    /**
     * Verifies the reset password key and retrieves the user. It checks if the provided reset password
     * key has expired. If not, it retrieves the user associated with the key from the database.
     *
     * @param key The verification key.
     * @return The verified user object.
     * @throws ApiException If the link is invalid, expired, or an error occurs.
     */
    @Override
    public User verifyResetPasswordKey(String key) {
        if (isLinkExpired(key, PASSWORD)) throw new ApiException("This link has expired. Please reset your password again.");

        try {
            User user = jdbc.queryForObject(SELECT_USER_BY_PASSWORD_URL_QUERY, Map.of("url", getVerificationUrl(key, PASSWORD.getType())), new UserRowMapper());
            //jdbc.update("DELETE_USER_FROM_PASSWORD_VERIFICATION_QUERY", Map.of("id", user.getId()));
            return user;

        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("This link is not valid. Please reset your password again.");

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    /**
     * Updates the password for a user. This method takes the user ID, new password,
     * and confirmation of the new password. It updates the user's password in the
     * database after ensuring the passwords match.
     *
     * @param userId             The ID of the user.
     * @param password           The new password.
     * @param confirmPassword    The confirmation of the new password.
     * @throws ApiException If there is an issue updating the password.
     */
    @Override
    public void renewPassword(Long userId, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) throw new ApiException("Passwords don't match. Please try again.");

        try {
            jdbc.update(UPDATE_USER_PASSWORD_BY_USER_ID_QUERY, Map.of("userId", userId, "password", encoder.encode(password)));
            jdbc.update(DELETE_PASSWORD_VERIFICATION_BY_USER_ID_QUERY, Map.of("userId", userId));

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    /**
     * Updates the password for a user.
     *
     * @param userId              The ID of the user.
     * @param currentPassword     The current password.
     * @param newPassword         The new password.
     * @param confirmNewPassword  The confirmation of the new password.
     * @throws ApiException If there is an issue updating the password.
     */
    @Override
    public void updatePassword(Long userId, String currentPassword, String newPassword, String confirmNewPassword) {
        if (!newPassword.equals(confirmNewPassword)) { throw new ApiException("Passwords don't match. Please try again."); }

        User user = get(userId);

        if (encoder.matches(currentPassword, user.getPassword())) {
            try {
                jdbc.update(UPDATE_USER_PASSWORD_BY_ID_QUERY, of("userId", userId, "password", encoder.encode(newPassword)));

            } catch (Exception exception) {
                throw new ApiException("An error occurred. Please try again.");
            }
        } else {
            throw new ApiException("Incorrect current password. Please try again.");
        }
    }

    /**
     * Verifies the account key and enables the user's account.
     *
     * @param key The verification key.
     * @return The verified user object.
     * @throws ApiException If the link is invalid.
     */
    @Override
    public User verifyAccountKey(String key) {
        try {
            User user = jdbc.queryForObject(SELECT_USER_BY_ACCOUNT_URL_QUERY, Map.of("url", getVerificationUrl(key, ACCOUNT.getType())), new UserRowMapper());
            jdbc.update(UPDATE_USER_ENABLED_QUERY, Map.of("enabled", true, "userId", user.getId()));
            return user;

        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("The link is invalid.");

        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    @Transactional
    public User toggleMfa(String email) {
        User user = getUserByEmail(email);
        if (isBlank(user.getPhone())) { throw new ApiException("You need a phone number to change Multi-Factor Authentication"); }
        user.setUsingMfa(!user.isUsingMfa());
        try {
            jdbc.update(TOGGLE_USER_MFA_QUERY, of("email", email, "isUsingMfa", user.isUsingMfa()));
            return user;

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("Unable to update Multi-Factor Authentication");
        }

    }

    @Override
    public void updateAccountSettings(Long userId, Boolean enabled, Boolean notLocked) {
        try {
            jdbc.update(UPDATE_USER_SETTINGS_QUERY, of("userId", userId, "enabled", enabled, "notLocked", notLocked));

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    /**
     * Toggles user notifications for the specified email.
     *
     * @param email The email of the user.
     * @return The updated user object.
     * @throws ApiException If there is an issue updating user notifications.
     */
    @Override
    public User toggleNotifications(String email) {
        User user = getUserByEmail(email);
        user.setNotificationsEnabled(!user.isNotificationsEnabled());
        try {
            jdbc.update(TOGGLE_USER_NOTIFICATIONS_QUERY, of("email", email, "notifEnabled", user.isNotificationsEnabled()));
            return user;

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("Unable to update Multi-Factor Authentication");
        }
    }

    /**
     * Updates the profile image of the user. This method sets a new profile image for
     * the user identified by the provided UserDTO. It saves the image file locally,
     * updates the user's profile image URL, and persists the changes in the database.
     *
     * @param user  The UserDTO containing user information.
     * @param image The profile image file to be set.
     * @throws ApiException If there is an issue updating the user's profile image.
     */
    @Override
    @Transactional
    public void updateImage(UserDTO user, MultipartFile image) {
        String userImageUrl = setUserImageUrl(user.email());
        saveImage(user.email(), image);
        jdbc.update(UPDATE_USER_PROFILE_IMAGE_QUERY, of("imageUrl", userImageUrl, "userId", user.id()));
    }

    /**
     * Verifies the expiration status of a given verification link based on the provided key
     * and verification type.
     *
     * @param key           The verification key.
     * @param verification  The type of verification (e.g., ACCOUNT).
     * @return True if the verification link has expired; otherwise, false.
     * @throws ApiException If there is an issue checking the expiration of the verification link.
     */
    private Boolean isLinkExpired(String key, VerificationType verification) {
        try {
            return jdbc.queryForObject(SELECT_EXPIRATION_BY_URL, Map.of("url", getVerificationUrl(key, verification.getType())), Boolean.class);

        } catch (EmptyResultDataAccessException exception) {
            log.error(exception.getMessage());
            throw new ApiException("This link is not valid. Please reset your password again");

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again");
        }
    }

    /**
     * Verifies the expiration status of a given verification code based on the provided code.
     *
     * @param code The verification code.
     * @return True if the verification code has expired; otherwise, false.
     * @throws ApiException If there is an issue checking the expiration of the verification code.
     */
    private Boolean isVerificationCodeExpired(String code) {
        try {
            return jdbc.queryForObject(SELECT_CODE_EXPIRATION_QUERY, Map.of("code", code), Boolean.class);

        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("This code is not valid. Please login again.");

        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    /**
     * Generates a SqlParameterSource for user details from an UpdateUserDetailsForm.
     * This method maps the fields of an UpdateUserDetailsForm to a SqlParameterSource
     * for use in database operations.
     *
     * @param updateUserDetailsForm The form containing updated user details.
     * @return The mapped SqlParameterSource for user details.
     */
    private SqlParameterSource getUserDetailsSqlParameterSource(UpdateUserDetailsForm updateUserDetailsForm) {
        return new MapSqlParameterSource()
                .addValue("user_id", updateUserDetailsForm.id())
                .addValue("firstName", updateUserDetailsForm.firstName())
                .addValue("lastName", updateUserDetailsForm.lastName())
                .addValue("email", updateUserDetailsForm.email())
                .addValue("phone", updateUserDetailsForm.phone())
                .addValue("county", updateUserDetailsForm.county())
                .addValue("city", updateUserDetailsForm.city())
                .addValue("address", updateUserDetailsForm.address())
                .addValue("bio", updateUserDetailsForm.bio());
    }

    /**
     * Generates a verification URL based on a key and verification type. It constructs
     * a verification URL using the provided key and verification type, which is useful
     * for various verification processes (e.g., account verification).
     *
     * @param key              The unique key for verification.
     * @param verificationType The type of verification (e.g., ACCOUNT).
     * @return The generated verification URL.
     */
    private String getVerificationUrl(String key, String verificationType) {
        return fromCurrentContextPath().path("/user/verify/" + verificationType + "/" + key).toUriString();
    }

    /**
     * Maps a User object to a SqlParameterSource for use in database operations.
     * It transforms a User object into a SqlParameterSource, making it
     * suitable for insertion or update operations in the database.
     *
     * @param user The User object to be mapped.
     * @return The mapped SqlParameterSource for the User object.
     */
    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("county", user.getCounty())
                .addValue("city", user.getCity())
                .addValue("password", encoder.encode(user.getPassword()));
    }

    /**
     * Retrieves the count of users with a given email address from the database.
     *
     * @param email The email address to check.
     * @return The count of users with the specified email.
     */
    private Integer getEmailCount(String email) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM rewards_app.users WHERE email = :email",
                Map.of("email", email), Integer.class);
    }

    /**
     * Saves the provided image for the user with the given email, storing the
     * the user's profile image locally, ensuring proper organization and management of
     * user images. It utilizes the email as a unique identifier for file naming.
     *
     * @param email The email of the user.
     * @param image The profile image file to be saved.
     * @throws ApiException If there is an issue saving the user's profile image.
     */
    private void saveImage(String email, MultipartFile image) {
        Path fileStorageLocation = Paths.get(System.getProperty("user.home") + "/Downloads/images/").toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation)) {
            try {
                Files.createDirectories(fileStorageLocation);
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new ApiException("Unable to create directories to save image");
            }
            log.info("Created directories: {}", fileStorageLocation);
        }

        try {
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(email + ".png"), REPLACE_EXISTING);
        } catch (IOException exception) {
            log.error(exception.getMessage());
            throw new ApiException(exception.getMessage());
        }
        log.info("File saved in: {} folder", fileStorageLocation);
    }

    /**
     * Sets the user image URL based on the user's email. This method constructs a
     * URL for accessing the user's profile image using the provided email.
     *
     * @param email The email of the user.
     * @return The generated URL for accessing the user's profile image.
     */
    private String setUserImageUrl(String email) {
        return fromCurrentContextPath().path("/user/image/" + email + ".png").toUriString();
    }

}
