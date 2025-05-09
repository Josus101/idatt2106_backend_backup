package org.ntnu.idatt2106.backend.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for admin login response.
 *
 * @Author Eskild Smestu
 * @since 0.2
 */
@Schema(description = "Response object for admin login")
@AllArgsConstructor
@Getter
@Setter
public class AdminLoginResponse {
  @Schema(description = "Token for the admin session", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwiaWF0IjoxNzQ2ODAwMzg5LCJleHAiOjE3NDY4MDc1ODl9.jsoi2H2xUHTVZ06-2FETEsJTora9_Lx_MFLEVdAKSA0")
  private String token;
  @Schema(description = "Username of the admin user", example = "admin")
  private boolean isSuperUser;
}
