package io.rewardsapp.rowmapper;

import io.rewardsapp.domain.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * RowMapper implementation for mapping ResultSet rows to User objects.
 */
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("user_id"))
                .firstName(resultSet.getString("first_name"))
                .lastName(resultSet.getString("last_name"))
                .email(resultSet.getString("email"))
                .password(resultSet.getString("password"))
                .county(resultSet.getString("county"))
                .city(resultSet.getString("city"))
                .address(resultSet.getString("address"))
                .phone(resultSet.getString("phone"))
                .bio(resultSet.getString("bio"))
                .notificationsEnabled(resultSet.getBoolean("notif_enabled"))
                .imageUrl(resultSet.getString("image_url"))
                .enabled(resultSet.getBoolean("enabled"))
                .usingMfa(resultSet.getBoolean("using_mfa"))
                .notLocked(resultSet.getBoolean("non_locked"))
                .createdAt(resultSet.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}
