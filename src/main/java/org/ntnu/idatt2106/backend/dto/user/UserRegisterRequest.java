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
    @Schema(description = "The email of the user")
    private String email;
    @Schema(description = "The password of the user")
    private String password;
    @Schema(description = "The first name of the user")
    private String firstname;
    @Schema(description = "The surname of the user")
    private String surname;
    @Schema(description = "The phoneNumber of the user")
    private String phoneNumber;

}