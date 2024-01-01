package io.rewardsapp.exception;

import com.auth0.jwt.exceptions.JWTDecodeException;
import io.rewardsapp.domain.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler implements ErrorController {

    private static final String ACCOUNT_LOCKED_MESSAGE = "Your account is currently locked. Please contact support for assistance.";
    private static final String DUPLICATE_ENTRY_MESSAGE = "The provided information already exists in our records.";
    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid credentials. Please check your email and password and try again.";
    private static final String ACCESS_DENIED_MESSAGE = "Access denied. You do not have permission to access this resource.";
    private static final String DECODE_TOKEN_FAILURE_MESSAGE = "Failed to decode the authentication token. Please try again or contact support.";
    private static final String RECORD_NOT_FOUND_MESSAGE = "Record not found. Please check the provided details.";

    private static final String ACCOUNT_ALREADY_VERIFIED_MESSAGE = "Your account has already been verified.";
    private static final String RESET_PASSWORD_EMAIL_ALREADY_SENT_MESSAGE = "An email to reset your password has already been sent. Please check your inbox.";
    private static final String DUPLICATE_ENTRY_UNIQUE_MESSAGE = "Duplicate entry. Please ensure the provided information is unique.";
    private static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred while processing your request.";

    /**
     * Handles internal exceptions and returns a customized HTTP response.
     *
     * @param exception The internal exception.
     * @param body The response body.
     * @param headers The HTTP headers.
     * @param statusCode The HTTP status code.
     * @param request The web request.
     * @return A ResponseEntity containing a customized HttpResponse.
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(exception.getMessage())
                        ._devMessage(exception.getMessage())
                        .status(resolve(statusCode.value()))
                        .statusCode(statusCode.value())
                        .build(), statusCode);
    }

    /**
     * Handles method argument validation exceptions and returns a customized HTTP response.
     *
     * @param exception The exception containing method argument validation errors.
     * @param headers The HTTP headers.
     * @param statusCode The HTTP status code.
     * @param request The web request.
     *
     * @return A ResponseEntity containing a customized HttpResponse.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        log.error(exception.getMessage());
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        String fieldMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(fieldMessage)
                        ._devMessage(exception.getMessage())
                        .status(resolve(statusCode.value()))
                        .statusCode(statusCode.value())
                        .build(), statusCode);
    }

    /**
     * Handles ApiException and returns a customized HTTP response.
     *
     * @param exception The ApiException.
     * @return A ResponseEntity containing a customized HttpResponse.
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<HttpResponse> handleApiException(ApiException exception) {
        log.error(exception.getMessage());
        return buildErrorResponse(exception.getMessage(), BAD_REQUEST);
    }

    /**
     * Handles LockedException and returns a customized HTTP response.
     *
     * @param exception The LockedException.
     * @return A ResponseEntity containing a customized HttpResponse.
     */
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> handleLockedException(LockedException exception) {
        log.error(exception.getMessage());
        return buildErrorResponse(ACCOUNT_LOCKED_MESSAGE, BAD_REQUEST);
    }

    /**
     * Handles SQLIntegrityConstraintViolationException and returns a customized HTTP response.
     *
     * @param exception The SQLIntegrityConstraintViolationException.
     * @return A ResponseEntity containing a customized HttpResponse.
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<HttpResponse> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException exception) {
        log.error(exception.getMessage());
        String errorMessage = exception.getMessage().contains("Duplicate entry") ? DUPLICATE_ENTRY_MESSAGE : exception.getMessage();
        return buildErrorResponse(errorMessage, BAD_REQUEST);
    }

    /**
     * Handles BadCredentialsException and returns a customized HTTP response.
     *
     * @param exception The BadCredentialsException.
     * @return A ResponseEntity containing a customized HttpResponse.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> handleBadCredentialsException(BadCredentialsException exception) {
        log.error(exception.getMessage());
        return buildErrorResponse(INVALID_CREDENTIALS_MESSAGE, BAD_REQUEST);
    }

    /**
     * Handles AccessDeniedException and returns a customized HTTP response.
     *
     * @param exception The AccessDeniedException.
     * @return A ResponseEntity containing a customized HttpResponse.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> handleAccessDeniedException(AccessDeniedException exception) {
        log.error(exception.getMessage());
        return buildErrorResponse(ACCESS_DENIED_MESSAGE, FORBIDDEN);
    }

    /**
     * Handles JWTDecodeException and returns a customized HTTP response.
     *
     * @param exception The JWTDecodeException.
     * @return A ResponseEntity containing a customized HttpResponse.
     */
    @ExceptionHandler(JWTDecodeException.class)
    public ResponseEntity<HttpResponse> handleJWTDecodeException(JWTDecodeException exception) {
        log.error(exception.getMessage());
        return buildErrorResponse(DECODE_TOKEN_FAILURE_MESSAGE, INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles EmptyResultDataAccessException and returns a customized HTTP response.
     *
     * @param exception The EmptyResultDataAccessException.
     * @return A ResponseEntity containing a customized HttpResponse.
     */
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<HttpResponse> handleEmptyResultDataAccessException(EmptyResultDataAccessException exception) {
        log.error(exception.getMessage());
        String errorMessage = exception.getMessage().contains("expected 1, actual 0") ? RECORD_NOT_FOUND_MESSAGE : exception.getMessage();
        return buildErrorResponse(errorMessage, BAD_REQUEST);
    }

    /**
     * Handles DataAccessException and returns a customized HTTP response.
     *
     * @param exception The DataAccessException.
     * @return A ResponseEntity containing a customized HttpResponse.
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<HttpResponse> handleDataAccessException(DataAccessException exception) {
        log.error(exception.getMessage());
        String processedErrorMessage = processExceptionMessage(exception.getMessage());
        return buildErrorResponse(processedErrorMessage, BAD_REQUEST);
    }

    /**
     * Builds a standardized error response.
     *
     * @param reason The reason for the error.
     * @param statusCode The HTTP status code.
     * @return A ResponseEntity containing a customized HttpResponse.
     */
    private ResponseEntity<HttpResponse> buildErrorResponse(String reason, HttpStatusCode statusCode) {
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .reason(reason)
                        ._devMessage(reason)
                        .status(resolve(statusCode.value()))
                        .statusCode(statusCode.value())
                        .build(), statusCode);
    }

    /**
     * Processes the exception message to provide more meaningful error messages.
     *
     * @param errorMessage The original error message.
     * @return The processed error message.
     */
    private String processExceptionMessage(String errorMessage) {
        if (errorMessage != null) {
            if (errorMessage.contains("Duplicate entry") && errorMessage.contains("AccountVerifications")) {
                return ACCOUNT_ALREADY_VERIFIED_MESSAGE;
            }

            if (errorMessage.contains("Duplicate entry") && errorMessage.contains("ResetPasswordVerifications")) {
                return RESET_PASSWORD_EMAIL_ALREADY_SENT_MESSAGE;
            }

            if (errorMessage.contains("Duplicate entry")) {
                return DUPLICATE_ENTRY_UNIQUE_MESSAGE;
            }
        }

        return UNEXPECTED_ERROR_MESSAGE;
    }
}
