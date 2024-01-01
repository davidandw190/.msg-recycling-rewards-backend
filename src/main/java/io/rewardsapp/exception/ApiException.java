package io.rewardsapp.exception;

/**
 * Used to represent exceptions related to API operations in the RecyclingRewards application.
 */
public class ApiException extends RuntimeException {
    public ApiException(String message) { super(message); }
}