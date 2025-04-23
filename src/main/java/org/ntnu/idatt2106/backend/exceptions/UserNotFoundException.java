package org.ntnu.idatt2106.backend.exceptions;

/**
 * Exception thrown when a user is not found.
 * This exception is used to indicate that the requested user does not exist in the system.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }

}
