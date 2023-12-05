package io.rewardsapp.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.rewardsapp.domain.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Component
@RequiredArgsConstructor
public class TokenProvider {
    private static final String MSG_CUSTOMER_SERVICE = ".msg Systems Customer Service";
    private static final String MSG_SYSTEMS_ROMANIA = ".msg Systems Romania";
    private static final String AUTHORITIES = "authorities";

    @Value("${jwt.secret}")
    private String SECRET;

    public static final long ACCESS_TOKEN_EXPIRATION_TIME = 100_000_000; //1_800_000;
    public static final long REFRESH_TOKEN_EXPIRATION_TIME = 432_000_000;

    /**
     * Creates an access token for the given user principal.
     *
     * @param userPrincipal The user principal for whom the access token is created.
     * @return The generated access token.
     */
    public String createAccessToken(UserPrincipal userPrincipal) {
        return JWT.create()
                .withIssuer(MSG_SYSTEMS_ROMANIA)
                .withAudience(MSG_CUSTOMER_SERVICE)
                .withIssuedAt(Date.from(Instant.now()))
                .withSubject(String.valueOf(userPrincipal.user().getId()))
                .withArrayClaim(AUTHORITIES, getClaimsFromUser(userPrincipal))
                .withExpiresAt(Date.from(Instant.now().plusMillis(ACCESS_TOKEN_EXPIRATION_TIME)))
                .sign(Algorithm.HMAC512(SECRET.getBytes()));
    }

    /**
     * Creates a refresh token for the given user principal.
     *
     * @param userPrincipal The user principal for whom the refresh token is created.
     * @return The generated refresh token.
     */
    public String createRefreshToken(UserPrincipal userPrincipal) {
        return JWT.create()
                .withIssuer(MSG_SYSTEMS_ROMANIA)
                .withAudience(MSG_CUSTOMER_SERVICE)
                .withIssuedAt(Date.from(Instant.now()))
                .withSubject(String.valueOf(userPrincipal.user().getId()))
                .withExpiresAt(Date.from(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION_TIME)))
                .sign(HMAC512(SECRET.getBytes()));
    }

    /* Retrieves the claims associated with a user principal. */
    private String[] getClaimsFromUser(UserPrincipal userPrincipal) {
        return userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);
    }

}
