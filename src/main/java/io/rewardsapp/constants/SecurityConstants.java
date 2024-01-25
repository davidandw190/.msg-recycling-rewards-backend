package io.rewardsapp.constants;

import org.springframework.stereotype.Component;

@Component
public class SecurityConstants {
    public static final String[] PUBLIC_URLS = {
            "/user/verify/password/**", "/user/login/**", "/user/register/**", "/user/verify/code/**",
            "/user/reset-pass/**", "/user/verify/account/**", "/user/refresh/token/**", "/user/image/**",
            "/user/new/password/**" , "/user/error/**", "eco-learn/resource/images/**", "eco-learn/resource/videos/**"
    };

    public static final long ACCESS_TOKEN_EXPIRATION_TIME = 100_000_000; //1_800_000;
    public static final long REFRESH_TOKEN_EXPIRATION_TIME = 432_000_000;

    public static final int PASS_ENCODER_STRENGTH = 14;
    public static final String MSG_CUSTOMER_SERVICE = ".msg Systems Customer Service";
    public static final String MSG_SYSTEMS_ROMANIA = ".msg Systems Romania";
    public static final String AUTHORITIES = "authorities";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
}
