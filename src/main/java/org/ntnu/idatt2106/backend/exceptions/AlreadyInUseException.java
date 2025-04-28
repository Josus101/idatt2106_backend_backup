package org.ntnu.idatt2106.backend.exceptions;

/**
 * Exception thrown when a resource is already in use.
 */
public class AlreadyInUseException extends RuntimeException{

    /**
     * Constructs a new AlreadyInUseException with the specified detail message.
     *
     * @param message the detail message
     */
    public AlreadyInUseException(String message) {
        super(message);
    }


}
