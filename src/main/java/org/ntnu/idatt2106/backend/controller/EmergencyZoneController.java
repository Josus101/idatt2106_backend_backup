package org.ntnu.idatt2106.backend.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.ntnu.idatt2106.backend.dto.emergencyZones.EmergencyZoneCreateDTO;
import org.ntnu.idatt2106.backend.dto.emergencyZones.EmergencyZoneFullDTO;
import org.ntnu.idatt2106.backend.service.EmergencyZoneService;
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
@RequestMapping("/api/emergency-zones")
public class EmergencyZoneController {

  @Autowired
  private EmergencyZoneService emergencyZoneService;

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
    List<EmergencyZoneFullDTO> emergencyZones = emergencyZoneService.getAllEmergencyZones();
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
  public ResponseEntity<?> getZonesInMapArea(
      @Parameter(
          description = "The map area to retrieve emergency zones from.",
          example = "",
          required = true
      ) @PathVariable List<List<Double>> mapArea,
      @PathVariable String excludedZoneIds) {
    return ResponseEntity.ok(emergencyZoneService.getZonesInMapArea(mapArea, excludedZoneIds));
  }

  @GetMapping("/zone/{zoneId}/point")
  public ResponseEntity<?> getZoneById(@PathVariable String zoneId) {
    return ResponseEntity.ok(emergencyZoneService.getZoneById(zoneId));
  }

  @GetMapping("/zone/{zoneId}/description")
  public ResponseEntity<?> getZoneDescription(@PathVariable String zoneId) {
    return ResponseEntity.ok(emergencyZoneService.getZoneDescription(zoneId));
  }


  @PostMapping("/zone/create/")
  public ResponseEntity<?> createZone(@RequestBody EmergencyZoneCreateDTO zone) {
    return ResponseEntity.ok(emergencyZoneService.createZone(zone));
  }

  @PutMapping("/zone/update/{zoneId}")
  public ResponseEntity<?> updateZone(@PathVariable String zoneId, @RequestBody EmergencyZoneCreateDTO zone) {
    return ResponseEntity.ok(emergencyZoneService.updateZone(zoneId, zone));
  }

  @DeleteMapping("/zone/delete/{zoneId}")
  public ResponseEntity<?> deleteZone(@PathVariable String zoneId) {
    return ResponseEntity.ok(emergencyZoneService.deleteZone(zoneId));
  }
}
