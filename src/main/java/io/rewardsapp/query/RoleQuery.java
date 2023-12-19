package io.rewardsapp.query;

import org.springframework.beans.factory.annotation.Value;

/**
 * Contains SQL queries related to roles in the SnapInvoice platform.
 */
public class RoleQuery {
    @Value("${spring.datasource.name}")
    private static String DB_NAME;

    public static final String SELECT_ROLES_QUERY = "SELECT * FROM " + DB_NAME + ".roles ORDER BY role_id";
    public static final String INSERT_ROLE_TO_USER_QUERY = "INSERT INTO " + DB_NAME + ".user_roles (user_id, role_id) VALUES (:userId, :roleId)";
    public static final String SELECT_ROLE_BY_NAME_QUERY = "SELECT * FROM " + DB_NAME + ".roles WHERE name = :name";
    public static final String SELECT_ROLE_BY_ID_QUERY = "SELECT r.role_id, r.name, r.permission FROM " + DB_NAME + ".roles r JOIN " + DB_NAME + ".user_roles ur ON ur.role_id = r.role_id JOIN " + DB_NAME + ".users u ON u.user_id = ur.user_id WHERE u.user_id = :userId";
    public static final String UPDATE_USER_ROLE_QUERY = "UPDATE " + DB_NAME + ".user_roles SET role_id = :roleId WHERE user_id = :userId";
}
