package org.ntnu.idatt2106.backend.dto.map.zones;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ntnu.idatt2106.backend.dto.map.CoordinatesDTO;

/**
 * Data transfer object for emergency zones with full details.
 * This class is used to transfer emergency zone data between the client and server.
 * It includes fields for the id, name, description, coordinates, type, and severity level of the emergency zone.
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data transfer object for creating an emergency zone")
public class ZoneFullDTO {
  @Schema(description = "ID of the emergency zone", example = "1")
  private Long id;

  @Schema(description = "Name of the emergency zone", example = "Brannsted")
  private String name;

  @Schema(description = "Description of the emergency zone",
      example = "Brann i området")
  private String description;

  @Schema(description = "Address of the emergency zone",
      example = "Høgskoleringen 5, 7034 Trondheim")
  private String address;

  @Schema(description = "Severity level of the emergency zone. 1-3 with 3 being severe and 1 being less severe",
      example = "1", defaultValue = "1")
  @Min(value = 1, message = "Severity level must be at least 1")
  @Max(value = 3, message = "Severity level must be at most 3")
  private int severityLevel;

  @Schema(description = "Type of the emergency zone", example = "Flom")
  private String type;

  @Schema(description = "Coordinates of the center of the emergency zone",
      example = "{ \"latitude\": 60.39299, \"longitude\": 5.32415 }")
  private CoordinatesDTO coordinates;

  @Schema(description = "Coordinates of the borders of the emergency zone. The inner lists are the coordinates" +
      " which makes up the rings that then make up the polygon. The outer list is the list of all polygons" +
      " associated with this zone.",
      example = "[[[[60.39299, 5.32415], [60.39299, 5.32415], [60.39299, 5.32415]]," +
          " [[60.39299, 5.32415], [60.39299, 5.32415], [60.39299, 5.32415]]]]")
  private String polygonCoordinateList;
}
