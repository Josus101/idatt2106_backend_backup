package org.ntnu.idatt2106.backend.dto.map.zones;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ntnu.idatt2106.backend.dto.map.CoordinatesDTO;

import java.util.List;


/**
 * Data transfer object for creating an emergency zone.
 * This class is used to transfer emergency zone data between the client and server.
 * It includes fields for the name, description, coordinates, type, and severity level of the emergency zone.
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data transfer object for creating an emergency zone")
public class MapZoneCreateDTO {
  @Schema(description = "Name of the emergency zone", example = "Crash site")
  private String name;

  @Schema(description = "Description of the emergency zone",
      example = "A crash site from a plane accident in the area")
  private String description;

  @Schema(description = "Address of the emergency zone", example = "Bergen, Norway")
  private String address;

  @Schema(description = "Coordinates of the center of the emergency zone",
      example = "[60.39299, 5.32415]")
  private CoordinatesDTO coordinates;

  @Schema(description = "Type of the emergency zone", example = "Fire, Flood, Power outage, etc.")
  private String type;

  @Schema(description = "Severity level of the emergency zone. 1-3 with 3 being severe and 1 being less severe",
      example = "1, 2, 3")
  private int severityLevel;

  @Schema(description = "Coordinates of the borders of the emergency zone. The inner lists are the coordinates" +
      " which makes up the rings that then make up the polygon. The outer list is the list of all polygons" +
      " associated with this zone.",
      example = "[[[[60.39299, 5.32415], [60.39299, 5.32415], [60.39299, 5.32415]]," +
          " [[60.39299, 5.32415], [60.39299, 5.32415], [60.39299, 5.32415]]]]")
  private List<List<List<CoordinatesDTO>>> polygonCoordinates;
}
