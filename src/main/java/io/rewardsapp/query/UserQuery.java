package io.rewardsapp.query;

public class UserQuery {
    public static final String SELECT_USER_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = :email";
    public static final String INSERT_VERIFICATION_QUERY = "INSERT INTO account_verifications (user_id, url) VALUES (:userId, :url)";
    public static final String SELECT_USER_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = :userId";
    public static final String COUNT_USER_EMAIL_QUERY = "SELECT COUNT(*) FROM users WHERE email = :email";
    public static final String DELETE_VERIFICATION_CODE_BY_USER_ID = "DELETE FROM tfa_verifications WHERE user_id = :userId";
    public static final String INSERT_VERIFICATION_CODE_QUERY = "INSERT INTO tfa_verifications (user_id, code, expiration_date) VALUES (:userId, :code, :expirationDate)";
}
