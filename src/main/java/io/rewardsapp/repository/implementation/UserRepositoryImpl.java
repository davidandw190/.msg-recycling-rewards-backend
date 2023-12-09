package io.rewardsapp.repository.implementation;

import io.rewardsapp.domain.Role;
import io.rewardsapp.domain.User;
import io.rewardsapp.domain.UserPrincipal;
import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.enums.VerificationType;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.form.UpdateUserForm;
import io.rewardsapp.repository.RoleRepository;
import io.rewardsapp.repository.UserRepository;
import io.rewardsapp.rowmapper.UserRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

import static io.rewardsapp.enums.RoleType.ROLE_USER;
import static io.rewardsapp.enums.VerificationType.ACCOUNT;
import static io.rewardsapp.enums.VerificationType.PASSWORD;
import static io.rewardsapp.query.UserQuery.*;
import static io.rewardsapp.utils.SmsUtils.sendSMS;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.time.DateFormatUtils.format;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.hibernate.type.descriptor.java.JdbcDateJavaType.DATE_FORMAT;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

/**
 * Implementation of UserRepository and UserDetailsService for handling User-related database operations.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository<User>, UserDetailsService {

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
    public User create(User user) {
        if (getEmailCount(user.getEmail().trim().toLowerCase()) > 0) {
            throw new ApiException("Email already in use. Please use a different email and try again.");
        }

        try {
            SimpleJdbcInsert insertUserQuery = new SimpleJdbcInsert(jdbc.getJdbcTemplate())
                    .withTableName("users")
                    .usingGeneratedKeyColumns("user_id");

            user.setCreatedAt(LocalDateTime.now());

            SqlParameterSource params = getSqlParameterSource(user);
            Number userId = insertUserQuery.executeAndReturnKey(params);
            user.setId(userId.longValue());
            log.info("User created with ID: {}", userId);
            roleRepository.addRoleToUser(user.getId(), ROLE_USER.name());
            user.setEnabled(false);
            user.setNotLocked(true);

            return user;

        } catch (Exception exception) {
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
     * Sends an account verification code to the user's phone number.
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
     * Creates an account verification code and stores it in the database.
     *
     * @param user The user for whom the verification code is created.
     */
    @Override
    public void createAccountVerificationCode(User user) {
        String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType());
        jdbc.update(INSERT_VERIFICATION_QUERY, Map.of("userId", user.getId(), "url", verificationUrl));
    }

    @Override
    public User updateUserDetails(UpdateUserForm updateUserForm) {
        try {
            jdbc.update(UPDATE_USER_DETAILS_QUERY, getUserDetailsSqlParameterSource(updateUserForm));
            return get(updateUserForm.id());

        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No User found by ID: " + updateUserForm.id());

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

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

    private boolean isVerificationCodeExpired(String code) {
        try {
            return Boolean.TRUE.equals(jdbc.queryForObject(SELECT_CODE_EXPIRATION_QUERY, Map.of("code", code), Boolean.class));

        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("This code is not valid. Please login again.");

        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    private SqlParameterSource getUserDetailsSqlParameterSource(UpdateUserForm updateUserForm) {
        return new MapSqlParameterSource()
                .addValue("user_id", updateUserForm.id())
                .addValue("firstName", updateUserForm.firstName())
                .addValue("lastName", updateUserForm.lastName())
                .addValue("email", updateUserForm.email())
                .addValue("phone", updateUserForm.phone())
                .addValue("city", updateUserForm.city())
                .addValue("address", updateUserForm.address())
                .addValue("title", updateUserForm.title())
                .addValue("bio", updateUserForm.bio());
    }

    /**
     * Generates a verification URL based on a key and verification type.
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
     *
     * @param user The user object to be mapped.
     * @return The mapped SqlParameterSource.
     */
    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
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
        return jdbc.queryForObject(COUNT_USER_EMAIL_QUERY, Map.of("email", email), Integer.class);
    }

}
