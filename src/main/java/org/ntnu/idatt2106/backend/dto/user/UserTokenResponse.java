package org.ntnu.idatt2106.backend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Data Transfer Object for user token")
public class UserTokenResponse {
  @Schema(description = "The token string")
  private String token;
  @Schema(description = "The expiration time of the token in milliseconds since epoch")
  private long expirationTime;

}
