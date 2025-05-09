package org.ntnu.idatt2106.backend.dto.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data transfer object for coordinates.
 * This class is used to transfer coordinates data between the client and server.
 * It includes fields for the latitude and longitude of the coordinates.
 *
 * @author André Merkesdal
 * @version 0.2
 * @since 0.2
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Coordinates of the center of the emergency zone",
    example = "{ \"latitude\": 60.39299, \"longitude\": 5.32415 }")
public class CoordinatesDTO {
  @Schema(description = "Latitude of the coordinates", example = "60.39299")
  private double latitude;

  @Schema(description = "Longitude of the coordinates", example = "5.32415")
  private double longitude;
}
