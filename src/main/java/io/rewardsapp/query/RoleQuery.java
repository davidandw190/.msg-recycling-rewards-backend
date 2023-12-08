package io.rewardsapp.query;

/**
 * Contains queries related to Role entities.
 */
public class RoleQuery {
    public static final String SELECT_ROLE_BY_NAME_QUERY = "SELECT * FROM roles WHERE name = :name";
}
