package org.ntnu.idatt2106.backend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object used for user login.
 * Contains essential user information like email and password.
 * @Author Konrad Seime
 * @since 0.2
 */
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Data Transfer Object for user login")
public class UserLoginRequest {
  @Schema(description = "The email of the user", example = "user@email.com")
  private String email;
  @Schema(description = "The password of the user", example = "password123")
  private String password;
}