package org.ntnu.idatt2106.backend.exceptions;

/**
 * Exception thrown when a user is not found.
 * This exception is used to indicate that the requested user does not exist in the system.
 * @Author Konrad Seime
 * @since 0.1
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructor for UserNotFoundException.
     * @param message The message to be displayed when the exception is thrown.
     */
    public UserNotFoundException(String message) {
        super(message);
    }

}
