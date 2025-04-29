package org.ntnu.idatt2106.backend.exceptions;

/**
 * Exception thrown when sending an email fails.
 */
public class MailSendingFailedException extends RuntimeException{
    /**
     * Constructor for MailSendingFailedException.
     *
     * @param message the detail message
     */
    public MailSendingFailedException(String message) {
        super(message);
    }

    /**
     * Constructor for MailSendingFailedException with a cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public MailSendingFailedException(String message, Throwable cause) {
      super(message, cause);
    }
}
