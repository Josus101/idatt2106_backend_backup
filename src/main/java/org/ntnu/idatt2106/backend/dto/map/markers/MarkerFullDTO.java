package org.ntnu.idatt2106.backend.dto.map.markers;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ntnu.idatt2106.backend.dto.map.CoordinatesDTO;

/**
 * Data transfer object for creating a marker.
 * This class is used to transfer marker data between the client and server.
 * It includes fields for the name, type, and coordinates of the marker.
 *
 * @author André Merkesdal
 * @version 0.2
 * @since 0.2
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data transfer object for creating a marker")
public class MarkerFullDTO {
  @Schema(description = "ID of the marker", example = "1")
  private Long id;

  @Schema(description = "Name of the marker", example = "Flystyrt")
  private String name;

  @Schema(description = "Description of the marker",
      example = "Fly crasjet i området")
  private String description;

  @Schema(description = "Address of the marker", example = "Høgskoleringen 5, 7034 Trondheim")
  private String address;

  @Schema(description = "Type of the marker", example = "Bunker")
  private String type;

  @Schema(description = "Coordinates of the marker",
      example = "{ \"latitude\": 60.39299, \"longitude\": 5.32415 }")
  private CoordinatesDTO coordinates;

}
