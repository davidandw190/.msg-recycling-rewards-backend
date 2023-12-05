package io.rewardsapp.security;

import org.springframework.stereotype.Component;

@Component
public class SecurityConstants {
    public static final String[] PUBLIC_URLS = {
            "/user/verify/password/**", "/user/login/**", "/user/register/**", "/user/verify/code/**",
            "/user/reset-password/**", "/user/verify/account/**", "/user/refresh/token/**", "/user/image/**",
            "/user/new/password/**" , "/user/error/**"
    };
}
