package io.rewardsapp.repository.implementation;

import io.rewardsapp.domain.Role;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.repository.RoleRepository;
import io.rewardsapp.rowmapper.RoleRowMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;

import static io.rewardsapp.enums.RoleType.ROLE_USER;
import static io.rewardsapp.query.RoleQuery.SELECT_ROLES_QUERY;
import static io.rewardsapp.query.RoleQuery.SELECT_ROLE_BY_NAME_QUERY;
import static java.util.Map.of;

/**
 * Implementation of RoleRepository for handling Role-related database operations.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository<Role> {
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Role create(Role data) {
        return null;
    }

    @Override
    public Collection<Role> list() {
        log.info("Fetching all roles");
        try {
            return jdbc.query(SELECT_ROLES_QUERY, new RoleRowMapper());

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public Role get(Long id) {
        return null;
    }

    @Override
    public Role update(Role data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    @Override
    public void addRoleToUser(Long userId, String roleName) {

    }

    @Override
    public Role getRoleByUserId(Long userId) {
        log.info("Fetching role for user id: {}", userId);
        try {
            return jdbc.queryForObject(SELECT_ROLE_BY_NAME_QUERY, of("userId", userId), new RoleRowMapper());

        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No role found by name: " + ROLE_USER.name());

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public Role getRoleByUserEmail(Long email) {
        return null;
    }

    @Override
    public void updateUserRole(Long userId, String roleName) {
    }
}
