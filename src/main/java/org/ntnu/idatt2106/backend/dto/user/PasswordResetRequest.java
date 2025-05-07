package org.ntnu.idatt2106.backend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object used for password reset requests.
 * Contains the new password for the user.
 * @Author Konrad Seime
 * @since 0.2
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequest {
  @Schema(description = "The new password for the user", example = "newPassword123")
  String password;
}
