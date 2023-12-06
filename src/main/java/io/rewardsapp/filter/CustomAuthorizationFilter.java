package io.rewardsapp.filter;

import io.rewardsapp.provider.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HTTP_OPTIONS_METHOD = "OPTIONS";
    private static final String[] PUBLIC_ROUTES = {
            "/user/new/password", "/user/login", "/user/verify/code", "/user/register", "/user/refresh/token", "/user/image"
    };

    /**
     * Filters incoming requests to authenticate based on JWT tokens.
     *
     * @param request     The HTTP request.
     * @param response    The HTTP response.
     * @param filterChain The filter chain.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)  {
        try {
            String token = getToken(request);
            Long userId = getUSerId(request);

            if (tokenProvider.isTokenValid(userId, token)) {
                List<GrantedAuthority> authorities = tokenProvider.getAuthorities(token);
                Authentication authentication = tokenProvider.getAuthentication(userId, authorities, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.clearContext();
            }
            filterChain.doFilter(request, response);

        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }

    private Long getUSerId(HttpServletRequest request) {
        return null;
        //TODO
    }

    private String getToken(HttpServletRequest request) {
        return null;
        //TODO
    }

    //TODO implement shouldNotFilter
}
