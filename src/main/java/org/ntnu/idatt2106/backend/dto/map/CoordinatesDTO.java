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
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data transfer object for coordinates")
public class CoordinatesDTO {
  @Schema(description = "Latitude of the coordinates", example = "60.39299")
  private double latitude;

  @Schema(description = "Longitude of the coordinates", example = "5.32415")
  private double longitude;
}
