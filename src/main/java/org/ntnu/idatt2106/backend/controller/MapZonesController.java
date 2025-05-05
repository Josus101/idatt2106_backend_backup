package org.ntnu.idatt2106.backend.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.ntnu.idatt2106.backend.dto.map.zones.EmergencyZoneCreateDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.EmergencyZoneDescDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.EmergencyZoneFullDTO;
import org.ntnu.idatt2106.backend.service.MapZonesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * THis class is used to handle all requests related to emergency zones.
 * It contains an endpoint for importing emergency zone data.
 *
 * @author André Merkesdal
 * @since 0.1
 */
@RestController
@RequestMapping("/api/map/zones")
public class MapZonesController {

  @Autowired
  private MapZonesService mapZonesService;

  /**
   * Endpoint for retrieving all emergency zones from the database.
   *
   * @return A list of all emergency zones.
   */
  @GetMapping("/all-zones")
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
              schema = @Schema(implementation = EmergencyZoneFullDTO.class)
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
    List<EmergencyZoneFullDTO> emergencyZones = mapZonesService.getAllEmergencyZones();
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
  @GetMapping("/all-zones/{mapArea}/{excludedZoneIds}")
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
              schema = @Schema(implementation = EmergencyZoneFullDTO.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "No emergency zones found in the specified area.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: No emergency zones found in the specified area.")
          )
      )
  })
  public ResponseEntity<?> getZonesInMapArea(
      @Parameter(
          description = "The map area to retrieve emergency zones from.",
          example = "",
          required = true
      ) @PathVariable List<List<Double>> mapArea,
      @Parameter(
          description = "The zone IDs to exclude from the response. Can be an empty array.",
          example = "[1, 2, 3]",
          required = true
      ) @PathVariable int[] excludedZoneIds) {
    List<EmergencyZoneFullDTO> emergencyZones = mapZonesService
        .getEmergencyZonesInMapArea(mapArea, excludedZoneIds);
    if (emergencyZones.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Error: No emergency zones found in the specified area.");
    } else {
      return ResponseEntity.ok(emergencyZones);
    }
  }

  /**
   * Endpoint for retrieving a specific emergency zone by its ID.
   *
   * @param zoneId The ID of the emergency zone to retrieve.
   * @return The emergency zone with the specified ID.
   */
  @GetMapping("/zone/{zoneId}/point")
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
              schema = @Schema(implementation = EmergencyZoneFullDTO.class)
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
      ) @PathVariable String zoneId) {
    try {
      EmergencyZoneFullDTO emergencyZone = mapZonesService.getEmergencyZoneById(zoneId);
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
              schema = @Schema(implementation = EmergencyZoneDescDTO.class)
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
      ) @PathVariable String zoneId) {
    try {
      EmergencyZoneDescDTO emergencyZoneDesc = mapZonesService.getEmergencyZoneDescription(zoneId);
      return ResponseEntity.ok(emergencyZoneDesc);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Emergency zone not found." + e.getMessage());
    }
  }


  @PostMapping("/zone/create/")
  public ResponseEntity<?> createZone(@RequestBody EmergencyZoneCreateDTO zone) {
    return ResponseEntity.ok(mapZonesService.createZone(zone));
  }

  @PutMapping("/zone/update/{zoneId}")
  public ResponseEntity<?> updateZone(@PathVariable String zoneId, @RequestBody EmergencyZoneCreateDTO zone) {
    return ResponseEntity.ok(mapZonesService.updateZone(zoneId, zone));
  }

  @DeleteMapping("/zone/delete/{zoneId}")
  public ResponseEntity<?> deleteZone(@PathVariable String zoneId) {
    return ResponseEntity.ok(mapZonesService.deleteZone(zoneId));
  }
}
