package org.ntnu.idatt2106.backend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "A response object for user admin operations")
public class UserAdminResponse {
  @Schema(description = "The id of the user", example = "1")
  private int id;
  @Schema(description = "The name of the user", example = "John Doe")
  private String name;
  @Schema(description = "Email of the user")
  private String email;
  @Schema(description = "Phone number of the user")
  private String phoneNumber;
  @Schema(description = "The households the user is a member of", example = "[apehuset, krekhuset]")
  private String[] households;

}
