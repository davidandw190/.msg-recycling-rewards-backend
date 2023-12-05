package io.rewardsapp.exception;

/**
 * The ApiException class is a custom exception that extends the RuntimeException class.
 * It is used to represent exceptions related to API operations in the recycling rewards application.
 */
public class ApiException extends RuntimeException {
    public ApiException(String message) { super(message); }
}