package org.ntnu.idatt2106.backend.exceptions;

public class UserNotAdminException extends RuntimeException{
    public UserNotAdminException(String message) {
        super(message);
    }

}
