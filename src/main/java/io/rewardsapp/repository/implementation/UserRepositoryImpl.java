package io.rewardsapp.repository.implementation;

import io.rewardsapp.domain.Role;
import io.rewardsapp.domain.User;
import io.rewardsapp.domain.UserPrincipal;
import io.rewardsapp.exception.ApiException;
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
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import static io.rewardsapp.enums.RoleType.ROLE_USER;
import static io.rewardsapp.enums.VerificationType.ACCOUNT;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository<User>, UserDetailsService {

    private static final String SELECT_USER_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = :email";
    private static final String INSERT_VERIFICATION_QUERY = "INSERT INTO account_verifications (user_id, url) VALUES (:userId, :url)";
    private static final String SELECT_USER_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = :userId";

    private final NamedParameterJdbcTemplate jdbc;
    private final RoleRepository<Role> roleRepository;
    private final BCryptPasswordEncoder encoder;

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
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType());
            jdbc.update(INSERT_VERIFICATION_QUERY, Map.of("userId", user.getId(), "url", verificationUrl));
            user.setEnabled(false);
            user.setNotLocked(true);

            return user;

        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    private String getVerificationUrl(String key, String verificationType) {
        return fromCurrentContextPath().path("/user/verify/" + verificationType + "/" + key).toUriString();
    }

    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("city", user.getCity())
                .addValue("password", encoder.encode(user.getPassword()));
    }

    private int getEmailCount(String lowerCase) {
        return 0;
    }

    @Override
    public Collection<User> list(int page, int pageSize) {
        return null;
    }

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
}
