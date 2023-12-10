package io.rewardsapp.utils;

import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import io.rewardsapp.domain.HttpResponse;
import io.rewardsapp.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

import static org.springframework.http.HttpStatus.*;

/**
 * Utility class for handling exceptions and producing appropriate HTTP responses.
 * This class encapsulates methods for processing various exceptions that may arise during API requests,
 * translating them into standardized HTTP responses for improved client feedback and debugging.
 */
@Slf4j
public class ExceptionUtils {
    /**
     * Processes the given exception and generates an appropriate HTTP response.
     *
     * @param request    The HTTP request.
     * @param response   The HTTP response.
     * @param exception  The exception to be processed.
     */
    public static void processError(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        if (exception instanceof ApiException || exception instanceof DisabledException ||
                exception instanceof LockedException || exception instanceof BadCredentialsException ||
                exception instanceof InvalidClaimException) {
            HttpResponse httpResponse = getHttpResponse(response, exception.getMessage(), BAD_REQUEST);
            writeResponse(response, httpResponse);

        } else if (exception instanceof TokenExpiredException) {
            HttpResponse httpResponse = getHttpResponse(response, exception.getMessage(), UNAUTHORIZED);
            writeResponse(response, httpResponse);

        } else {
            HttpResponse httpResponse = getHttpResponse(response, "An error occurred. Please try again.", INTERNAL_SERVER_ERROR);
            writeResponse(response, httpResponse);
        }

        log.error(exception.getMessage());
    }


    private static HttpResponse getHttpResponse(HttpServletResponse response, String message, HttpStatus httpStatus) {
    }

    private static void writeResponse(HttpServletResponse response, HttpResponse httpResponse) {
    }
}
