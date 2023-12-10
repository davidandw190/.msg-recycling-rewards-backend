package io.rewardsapp.utils;

import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.rewardsapp.domain.HttpResponse;
import io.rewardsapp.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

import java.io.OutputStream;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

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
    public static void handleException(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        if (isClientError(exception)) {
            HttpResponse clientErrorResponse = buildClientErrorResponse(exception.getMessage(), BAD_REQUEST);
            sendResponse(response, clientErrorResponse);
        } else if (isTokenExpiredException(exception)) {
            HttpResponse unauthorizedResponse = buildUnauthorizedResponse(exception.getMessage());
            sendResponse(response, unauthorizedResponse);
        } else {
            HttpResponse serverErrorResponse = buildServerErrorResponse();
            sendResponse(response, serverErrorResponse);
        }

        log.error("Exception occurred during request processing: {}", exception.getMessage());
    }


    private static boolean isClientError(Exception exception) {
        return exception instanceof ApiException ||
                exception instanceof DisabledException ||
                exception instanceof LockedException ||
                exception instanceof BadCredentialsException ||
                exception instanceof InvalidClaimException;
    }

    private static boolean isTokenExpiredException(Exception exception) {
        return exception instanceof TokenExpiredException;
    }

    private static HttpResponse buildClientErrorResponse(String message, HttpStatus httpStatus) {
        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .reason(message)
                .status(httpStatus)
                .statusCode(httpStatus.value())
                .build();
    }

    private static HttpResponse buildUnauthorizedResponse(String message) {
        return buildClientErrorResponse(message, UNAUTHORIZED);
    }

    private static HttpResponse buildServerErrorResponse() {
        return buildClientErrorResponse("An error occurred. Please try again.", INTERNAL_SERVER_ERROR);
    }

    private static void sendResponse(HttpServletResponse response, HttpResponse httpResponse) {
        try (OutputStream outputStream = response.getOutputStream()) {
            configureResponse(response, httpResponse);
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(outputStream, httpResponse);
            outputStream.flush();
        } catch (Exception e) {
            log.error("Error writing HTTP response: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private static void configureResponse(HttpServletResponse response, HttpResponse httpResponse) {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(httpResponse.getStatus().value());
    }
}
