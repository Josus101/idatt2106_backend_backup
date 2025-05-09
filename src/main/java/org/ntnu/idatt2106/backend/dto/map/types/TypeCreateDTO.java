package org.ntnu.idatt2106.backend.dto.map.types;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data transfer object for creating a new type.
 * This class is used to transfer type data between the client and server.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypeCreateDTO {
  @Schema(description = "Name of the type", example = "Flom")
  private String type;
}
