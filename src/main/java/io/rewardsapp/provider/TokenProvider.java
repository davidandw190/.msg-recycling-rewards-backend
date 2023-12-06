package io.rewardsapp.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import io.rewardsapp.domain.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class TokenProvider {
    private static final String MSG_CUSTOMER_SERVICE = ".msg Systems Customer Service";
    private static final String MSG_SYSTEMS_ROMANIA = ".msg Systems Romania";
    private static final String AUTHORITIES = "authorities";
    private static final String TOKEN_CANNOT_BE_VERIFIED = "";

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

    /**
     * Extracts the subject (user ID) from the provided JWT token.
     *
     * @param token   The JWT token from which to extract the subject.
     * @param request The HTTP servlet request.
     * @return The subject (user ID) extracted from the token.
     * @throws TokenExpiredException If the token has expired.
     * @throws InvalidClaimException If the token has invalid claims.
     */
    public Long getSubject(String token, HttpServletRequest request) {
        try {
            return Long.valueOf(getJWTVerifier().verify(token).getSubject());
        } catch (TokenExpiredException exception) {
            request.setAttribute("expiredMessage", exception.getMessage());
            throw exception;
        } catch (InvalidClaimException exception) {
            request.setAttribute("invalidClaim", exception.getMessage());
            throw exception;
        } catch (Exception exception) {
            throw exception;
        }
    }

    /* Retrieves the authorities from the provided JWT token. */
    public List<GrantedAuthority> getAuthorities(String token) {
        return stream(getClaimsFromToken(token)).map(SimpleGrantedAuthority::new).collect(toList());
    }

    /* Retrieves the claims from the provided JWT token. */
    private String[] getClaimsFromToken(String token) {
        JWTVerifier tokenVerifier = getJWTVerifier();
        return tokenVerifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    /* Retrieves the claims associated with a user principal. */
    private String[] getClaimsFromUser(UserPrincipal userPrincipal) {
        return userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);
    }

    /* Retrieves a JWT verifier instance with the specified algorithm and issuer. */
    private JWTVerifier getJWTVerifier() {
        JWTVerifier tokenVerifier;
        try {
            Algorithm algorithm = HMAC512(SECRET);
            tokenVerifier = JWT.require(algorithm).withIssuer(MSG_SYSTEMS_ROMANIA).build();
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }

        return tokenVerifier;
    }
}
