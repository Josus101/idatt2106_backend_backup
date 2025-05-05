package org.ntnu.idatt2106.backend.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for admin user requests.
 *
 * @Author Jonas Reiher
 * @since 0.2
 */
@Schema(description = "Request object for admin users")
@AllArgsConstructor
@Getter
@Setter
public class AdminGetResponse {
  @Schema(description = "ID of the admin user", example = "1")
  private int id;
  @Schema(description = "Username of the admin user", example = "admin")
  private String username;
  @Schema(description = "Password of the admin user", example = "password")
  private boolean isSuperUser;


}
