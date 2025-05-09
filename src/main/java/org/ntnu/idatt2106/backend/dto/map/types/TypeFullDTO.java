package org.ntnu.idatt2106.backend.dto.map.types;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data transfer object for a full type object with id and name.
 * This class is used to transfer type data between the client and server.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypeFullDTO {
  @Schema(description = "ID of the type", example = "1")
  private int id;

  @Schema(description = "Name of the type", example = "Flom")
  private String type;
}
