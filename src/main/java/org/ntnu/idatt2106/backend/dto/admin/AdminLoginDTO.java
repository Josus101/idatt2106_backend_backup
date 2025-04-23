package org.ntnu.idatt2106.backend.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Data transfer object for admin login credentials")
public class AdminLoginDTO {
  @Schema(description = "Username of the admin", example = "admin")
  private String username;
  @Schema(description = "Password of the admin", example = "admin123")
  private String password;
}
