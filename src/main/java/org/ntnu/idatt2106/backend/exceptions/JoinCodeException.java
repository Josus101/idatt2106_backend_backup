package org.ntnu.idatt2106.backend.exceptions;

/**
 * Exception thrown when an error occurs while generating a join code.
 *
 * @author Konrad Seime
 * @version 0.2
 * @since 0.2
 */
public class JoinCodeException extends RuntimeException{
    /**
     * Constructs a new JoinCodeException with the specified detail message.
     *
     * @param message the detail message
     */
    public JoinCodeException(String message) {
        super(message);
    }

}
