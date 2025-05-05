package org.ntnu.idatt2106.backend.dto.emergencyZones;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Data transfer object for emergency zone descriptions.
 * This class is used to transfer emergency zone data between the client and server.
 * It includes fields for the name, description, type, and severity level of the emergency zone.
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data transfer object for emergency zone descriptions")
public class EmergencyZoneDescDTO {
  @Schema(description = "Name of the emergency zone", example = "Crash site")
  private String name;

  @Schema(description = "Description of the emergency zone",
      example = "A crash site from a plane accident in the area")
  private String description;

  @Schema(description = "The address of the emergency zone, based on the coordinates of the centered point",
      example = "Bergen, Norway")
  private String address;
}
