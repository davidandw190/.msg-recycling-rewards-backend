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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository<User>, UserDetailsService {

    public static final String SELECT_USER_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = :email";

    private final NamedParameterJdbcTemplate jdbc;
    private final RoleRepository<Role> roleRepository;

    @Override
    public User create(User date) {
        return null;
    }

    @Override
    public Collection<User> list(int page, int pageSize) {
        return null;
    }

    @Override
    public User get(Long id) {
        return null;
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
