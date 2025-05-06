package org.ntnu.idatt2106.backend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.messaging.handler.annotation.SendTo;

/**
 * A minimal representation of a user.
 * This class is used to represent a user with only the id and name fields.
 */
@Schema(description = "A minimal representation of a user")
@Getter
@Setter
@AllArgsConstructor
public class UserMinimalGetResponse {
  @Schema(description = "The id of the user", example = "1")
  private int id;
  @Schema(description = "The name of the user", example = "John Doe")
  private String name;
}
