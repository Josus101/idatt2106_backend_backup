package org.ntnu.idatt2106.backend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object used for user token response.
 * Contains the token string and its expiration time.
 * @Author Konrad Seime
 * @since 0.1
 */
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Data Transfer Object for user token")
public class UserTokenResponse {
  @Schema(description = "The token string", example = "c21vhb32jn4qtiperhu.hjoewigudbs8y97vgoy8lhbiqj3")
  private String token;
  @Schema(description = "The expiration time of the token in milliseconds since epoch", example = "1713888000000")
  private long expirationTime;

}
