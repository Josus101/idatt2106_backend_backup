package org.ntnu.idatt2106.backend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Data transfer object representing user registration data")
public class UserRegisterRequest {
    @Schema(description = "The email of the user", example = "user@email.com")
    private String email;
    @Schema(description = "The password of the user", example = "password123")
    private String password;
    @Schema(description = "The first name of the user", example = "Elon")
    private String firstname;
    @Schema(description = "The last of the user", example = "Yuck")
    private String lastname;
    @Schema(description = "The phoneNumber of the user", example = "88888888")
    private String phoneNumber;

}