package io.rewardsapp.filter;

import io.rewardsapp.provider.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Arrays;
import java.util.List;

import static io.rewardsapp.utils.ExceptionUtils.handleException;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    public static final String TOKEN_PREFIX = "Bearer ";
    private static final String HTTP_OPTIONS_METHOD = "OPTIONS";
    private static final String[] PUBLIC_ROUTES = {
            "/user/new/password", "/user/login", "/user/verify/code", "/user/register", "/user/refresh/token", "/user/image",
            "/user/reset-pass", "eco-lean/resource/image"
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
            handleException(request, response, exception);
        }
    }

    /**
     * Determines whether to skip filtering for certain requests.
     *
     * @param request The HTTP request.
     * @return True if filtering should be skipped, false otherwise.
     * @throws ServletException If a servlet-specific error occurs.
     */
    @Override
    public boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        log.info("Request URI: {}", request.getRequestURI());
        log.info("Public Routes: {}", Arrays.toString(PUBLIC_ROUTES));

        return request.getHeader(AUTHORIZATION) == null ||
                !request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX) ||
                request.getMethod().equalsIgnoreCase(HTTP_OPTIONS_METHOD) ||
                asList(PUBLIC_ROUTES).contains(request.getRequestURI());
    }

    /* Extracts the user ID from the JWT token. */
    private Long getUSerId(HttpServletRequest request) {
        return tokenProvider.getSubject(getToken(request), request);
    }

    /* Extracts the JWT token from the request headers. */
    private String getToken(HttpServletRequest request) {
        return ofNullable(request.getHeader(AUTHORIZATION))
                .filter(header -> header.startsWith(TOKEN_PREFIX))
                .map(token -> token.replace(TOKEN_PREFIX, EMPTY))
                .orElse(null);
    }
}