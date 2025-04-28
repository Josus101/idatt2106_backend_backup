package org.ntnu.idatt2106.backend.exceptions;

/**
 * Exception thrown when a user is not authorized to perform an action.
 * This exception is used to indicate that the user does not have the necessary permissions
 * to access a resource or perform an operation.
 * @Author Konrad Seime
 * @since 0.1
 */
public class UnauthorizedException extends RuntimeException{

    /**
     * Constructor for UnauthorizedException.
     * @param message The message to be displayed when the exception is thrown.
     */
    public UnauthorizedException(String message) {
        super(message);
    }

}
