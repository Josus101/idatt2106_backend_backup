package org.ntnu.idatt2106.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object used for registering a new user.
 * Contains essential user information like email, password, and contact details.
 */
@AllArgsConstructor
@Getter
@Setter
public class UserRegisterDTO {
    private String email;
    private String password;
    private String firstname;
    private String surname;
    private String phoneNumber;

}