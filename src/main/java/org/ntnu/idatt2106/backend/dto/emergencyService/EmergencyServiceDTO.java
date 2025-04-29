package org.ntnu.idatt2106.backend.dto.emergencyService;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object representing an emergency service.
 * This class is used to transfer emergency service data between the client and server.
 * @author Erlend Eide Zindel
 * @since 1.0
 */
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "DTO for EmergencyService")
public class EmergencyServiceDTO {
    @Schema(description = "Name of the service", example = "Torget 6")
    private String name;

    @Schema(description = "Description of the service", example = "Plasser: 390")
    private String description;

    @Schema(description = "Latitude", example = "59.123456")
    private double latitude;

    @Schema(description = "Longitude", example = "10.654321")
    private double longitude;

    @Schema(description = "Type of the service", example = "Bunker")
    private String type;
}
