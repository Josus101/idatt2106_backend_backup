package org.ntnu.idatt2106.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.ntnu.idatt2106.backend.dto.map.CoordinatesDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.MapZoneCreateDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.MapZoneDescDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.MapZoneFullDTO;
import org.ntnu.idatt2106.backend.service.MapEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * This class is used to handle all requests related to emergency zones.
 * It contains an endpoint for importing emergency zone data.
 *
 * @author André Merkesdal
 * @since 0.1
 */
@RestController
@RequestMapping("/api/map")
public class MapController {

  @Autowired
  private MapEntityService mapEntityService;

  /**
   * Endpoint for retrieving all emergency zones from the database.
   *
   * @return A list of all emergency zones.
   */
  @GetMapping("/zones")
  @Operation(
      summary = "Get all emergency zones",
      description = "Retrieves all emergency zones from the database."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Emergency zones retrieved successfully.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = MapZoneFullDTO.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "No emergency zones found.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: No emergency zones found.")
          )
      )
  })
  public ResponseEntity<?> getEmergencyZones() {
    List<MapZoneFullDTO> emergencyZones = mapEntityService.getAllMapZones();
    if (emergencyZones.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: No emergency zones found.");
    }
    return ResponseEntity.ok(emergencyZones);
  }

  /**
   * Endpoint for retrieving all emergency zones in a specific map area.
   *
   * @param mapArea The map area to retrieve emergency zones from.
   * @param excludedZoneIds The zone IDs to exclude from the response (which have already been retrieved).
   * @return A list of emergency zones in the specified map area.
   */
  @GetMapping("/zones/{mapArea}/{excludedZoneIds}")
  @Operation(
      summary = "Get emergency zones in map area",
      description = "Retrieves emergency zones in a specific map area."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Emergency zones retrieved successfully.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = MapZoneFullDTO.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "No emergency zones found in the specified area.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: No emergency zones found in the specified area.")
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid request parameters.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Map area cannot be null or empty.")
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: An unexpected error occurred.")
          )
      )
  })
  public ResponseEntity<?> getZonesInMapArea(
      @Parameter(
          description = "The map area to retrieve emergency zones from.",
          example = "",
          required = true
      ) @PathVariable List<CoordinatesDTO> mapArea,
      @Parameter(
          description = "The zone IDs to exclude from the response. Can be an empty array.",
          example = "[1, 2, 3]",
          required = true
      ) @PathVariable Long[] excludedZoneIds) {
    try {
      if (mapArea == null || mapArea.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Error: Map area cannot be null or empty.");
      }

      List<MapZoneFullDTO> emergencyZones = mapEntityService.getMapZonesInMapArea(mapArea, excludedZoneIds);

      if (emergencyZones.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("Error: No emergency zones found in the specified area.");
      }
      return ResponseEntity.ok(emergencyZones);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Error: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error: An unexpected error occurred. " + e.getMessage());
    }
  }

  /**
   * Endpoint for retrieving a specific emergency zone by its ID.
   *
   * @param zoneId The ID of the emergency zone to retrieve.
   * @return The emergency zone with the specified ID.
   */
  @GetMapping("/zone/{zoneId}")
  @Operation(
      summary = "Get emergency zone by ID",
      description = "Retrieves a specific emergency zone by its ID."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Emergency zone retrieved successfully.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = MapZoneFullDTO.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Emergency zone not found.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Emergency zone not found.")
          )
      )
  })
  public ResponseEntity<?> getZoneById(
      @Parameter(
          description = "The ID of the emergency zone to retrieve.",
          example = "12345",
          required = true
      ) @PathVariable Long zoneId) {
    try {
      MapZoneFullDTO emergencyZone = mapEntityService.getMapZoneById(zoneId);
      return ResponseEntity.ok(emergencyZone);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Emergency zone not found." + e.getMessage());
    }
  }

  /**
   * Endpoint for retrieving the description of a specific emergency zone by its ID.
   *
   * @param zoneId The ID of the emergency zone to retrieve the description from.
   * @return The description of the emergency zone with the specified ID.
   */
  @GetMapping("/zone/{zoneId}/description")
  @Operation(
      summary = "Get emergency zone description by ID",
      description = "Retrieves the description of a specific emergency zone by its ID."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Emergency zone description retrieved successfully.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = MapZoneDescDTO.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Emergency zone not found.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Emergency zone not found.")
          )
      )
  })
  public ResponseEntity<?> getZoneDescription(
      @Parameter(
          description = "The ID of the emergency zone to retrieve the description from.",
          example = "12345",
          required = true
      ) @PathVariable Long zoneId) {
    try {
      MapZoneDescDTO emergencyZoneDesc = mapEntityService.getMapZoneDescById(zoneId);
      return ResponseEntity.ok(emergencyZoneDesc);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Emergency zone not found." + e.getMessage());
    }
  }

  /**
   * Endpoint for creating a new emergency zone.
   *
   * @param zone The emergency zone to create.
   * @return The ID of the newly created emergency zone.
   */
  @PostMapping("/zone/create/")
  @Operation(
      summary = "Create a new emergency zone",
      description = "Creates a new emergency zone."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "Emergency zone created successfully.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = Long.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid request parameters.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Invalid request parameters.")
          )
      )
  })
  public ResponseEntity<?> createZone(
      @Parameter(
          description = "The emergency zone to create.",
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = MapZoneCreateDTO.class)
          )
      ) @RequestBody MapZoneCreateDTO zone) {
    try {
      Long zoneId = mapEntityService.createZone(zone);
      return ResponseEntity.status(HttpStatus.CREATED).body(zoneId);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    }
  }

  /**
   * Endpoint for updating an existing emergency zone.
   *
   * @param zoneId The ID of the emergency zone to update.
   * @param zone The updated emergency zone data.
   * @return A success message.
   */
  @PutMapping("/zone/update/{zoneId}")
  @Operation(
      summary = "Update an existing emergency zone",
      description = "Updates an existing emergency zone."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Emergency zone updated successfully.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = String.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid request parameters.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Invalid request parameters.")
          )
      )
  })
  public ResponseEntity<?> updateZone(
      @Parameter(
          description = "The ID of the emergency zone to update.",
          example = "12345",
          required = true
      ) @PathVariable Long zoneId,
      @Parameter(
          description = "The updated emergency zone data.",
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = MapZoneCreateDTO.class)
          )
      ) @RequestBody MapZoneCreateDTO zone) {
    try {
      mapEntityService.updateZone(zoneId, zone);
      return ResponseEntity.ok("Zone updated successfully.");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    }
  }

  /**
   * Endpoint for deleting an emergency zone.
   *
   * @param zoneId The ID of the emergency zone to delete.
   * @return A success message.
   */
  @DeleteMapping("/zone/delete/{zoneId}")
  @Operation(
      summary = "Delete an emergency zone",
      description = "Deletes an emergency zone."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Emergency zone deleted successfully.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = String.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid request parameters.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Invalid request parameters.")
          )
      )
  })
  public ResponseEntity<?> deleteZone(
      @Parameter(
          description = "The ID of the emergency zone to delete.",
          example = "12345",
          required = true
      ) @PathVariable Long zoneId) {
    try {
      mapEntityService.deleteZone(zoneId);
      return ResponseEntity.ok("Zone deleted successfully.");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    }
  }
}
