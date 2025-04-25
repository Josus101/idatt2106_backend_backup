package org.ntnu.idatt2106.backend.exceptions;

/**
 * Exception thrown when a token has expired.
 * This exception is used to indicate that the provided token is no longer valid
 * and the user needs to re-authenticate or obtain a new token.
 * @Author Konrad Seime
 * @since 0.1
 */
public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}
