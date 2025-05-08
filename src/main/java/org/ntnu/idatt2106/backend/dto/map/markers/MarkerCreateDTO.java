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
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data transfer object for creating a marker")
public class MarkerCreateDTO {
  @Schema(description = "Name of the marker", example = "Crash site")
  private String name;

  @Schema(description = "Description of the marker",
      example = "A crash site from a plane accident in the area")
  private String description;

  @Schema(description = "Address of the marker", example = "Bergen, Norway")
  private String address;

  @Schema(description = "Type of the marker", example = "Bunker, Defibrillator, etc.")
  private String type;

  @Schema(description = "Coordinates of the marker",
      example = "{ \"latitude\": 60.39299, \"longitude\": 5.32415 }")
  private CoordinatesDTO coordinates;
}
