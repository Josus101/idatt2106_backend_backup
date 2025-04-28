package org.ntnu.idatt2106.backend.exceptions;

/**
 * Exception thrown when a user is not verified.
 */
public class UserNotVerifiedException extends RuntimeException{
    /**
     * Constructs a new UserNotVerifiedException with the specified detail message.
     *
     * @param message the detail message
     */
    public UserNotVerifiedException(String message) {
        super(message);
    }

    /**
     * Constructs a new UserNotVerifiedException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public UserNotVerifiedException(String message, Throwable cause) {
        super(message, cause);
    }

}
