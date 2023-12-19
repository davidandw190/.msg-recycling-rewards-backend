package io.rewardsapp.query;

/**
 * Contains queries related to User entities.
 */
public class UserQuery {

    public static final String INSERT_USER_QUERY = "INSERT INTO users (first_name, last_name, email, password, city) VALUES (:firstName, :lastName, :email, :password, :city)";
    public static final String SELECT_USER_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = :email";
    public static final String INSERT_VERIFICATION_QUERY = "INSERT INTO account_verifications (user_id, url) VALUES (:userId, :url)";
    public static final String SELECT_USER_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = :userId";
    public static final String COUNT_USER_EMAIL_QUERY = "SELECT COUNT(*) FROM users WHERE email = :email";
    public static final String DELETE_VERIFICATION_CODE_BY_USER_ID = "DELETE FROM tfa_verifications WHERE user_id = :userId";
    public static final String INSERT_VERIFICATION_CODE_QUERY = "INSERT INTO tfa_verifications (user_id, code, expiration_date) VALUES (:userId, :code, :expirationDate)";
    public static final String UPDATE_USER_DETAILS_QUERY = "UPDATE users SET first_name = :firstName, last_name = :lastName, email = :email, city = :city, phone = :phone, address = :address, bio = :bio WHERE user_id = :user_id";
    public static final String SELECT_USER_BY_USER_CODE_QUERY = "SELECT * FROM users WHERE user_id = (SELECT user_id FROM tfa_verifications WHERE code = :code)";
    public static final String DELETE_CODE = "DELETE FROM tfa_verifications WHERE code = :code";
    public static final String SELECT_CODE_EXPIRATION_QUERY = "SELECT expiration_date < NOW() AS is_expired FROM tfa_verifications WHERE code = :code";
    public static final String DELETE_PASSWORD_VERIFICATION_BY_USER_ID_QUERY = "DELETE FROM reset_pass_verifications WHERE user_id = :userId";
    public static final String INSERT_PASSWORD_VERIFICATION_QUERY = "INSERT INTO reset_pass_verifications (user_id, url, expiration_date) VALUES (:userId, :url, :expirationDate)";
    public static final String SELECT_USER_BY_PASSWORD_URL_QUERY = "SELECT * FROM users WHERE user_id = (SELECT user_id FROM reset_pass_verifications WHERE url = :url)";
    public static final String SELECT_EXPIRATION_BY_URL = "SELECT expiration_date < NOW() AS is_expired FROM reset_pass_verifications WHERE url = :url";
    public static final String UPDATE_USER_PASSWORD_BY_USER_ID_QUERY = "UPDATE users SET password = :password WHERE user_id = :userId";
    public static final String UPDATE_USER_PASSWORD_BY_ID_QUERY = "UPDATE users SET password = :password WHERE user_id = :userId";
    public static final String SELECT_USER_BY_ACCOUNT_URL_QUERY = "SELECT * FROM users WHERE user_id = (SELECT user_id FROM account_verifications WHERE url = :url)";
    public static final String UPDATE_USER_ENABLED_QUERY = "UPDATE users SET enabled = :enabled WHERE userId = :userId";
    public static final String TOGGLE_USER_MFA_QUERY = "UPDATE users SET using_mfa = :isUsingMfa WHERE email = :email";
    public static final String UPDATE_USER_SETTINGS_QUERY = "UPDATE users SET enabled = :enabled, non_locked = :notLocked WHERE user_id = :userId";
    public static final String TOGGLE_USER_NOTIFICATIONS_QUERY = "UPDATE users SET notif_enabled = :notifEnabled WHERE email = :email";
    public static final String UPDATE_USER_PROFILE_IMAGE_QUERY = "UPDATE users SET image_url = :imageUrl WHERE user_id = :userId";
}
