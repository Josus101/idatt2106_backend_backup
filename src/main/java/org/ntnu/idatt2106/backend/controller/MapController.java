package org.ntnu.idatt2106.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.ntnu.idatt2106.backend.dto.map.CoordinatesDTO;
import org.ntnu.idatt2106.backend.dto.map.MapEntityDescDTO;
import org.ntnu.idatt2106.backend.dto.map.QueryRequestInArea;
import org.ntnu.idatt2106.backend.dto.map.markers.MarkerCreateDTO;
import org.ntnu.idatt2106.backend.dto.map.markers.MarkerFullDTO;
import org.ntnu.idatt2106.backend.dto.map.types.TypeFullDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.ZoneCreateDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.ZoneFullDTO;
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
              schema = @Schema(implementation = ZoneFullDTO.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "No emergency zones found.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: No emergency zones found.")
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Internal server error occurred.")
          )
      )
  })
  public ResponseEntity<?> getEmergencyZones() {
    try {
      List<ZoneFullDTO> emergencyZones = mapEntityService.getAllMapZones();
      if (emergencyZones.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: No emergency zones found.");
      }
      return ResponseEntity.ok(emergencyZones);
    } catch (Exception e) {

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Internal server error occurred. " + e.getMessage());
    }
  }

  /**
   * Endpoint for retrieving emergency zones in a specific map area.
   *
   * @param request Contains map area and excluded zone IDs
   * @return A list of emergency zones in the specified map area.
   */
  @PostMapping("/zones/in-area")
  @Operation(
      summary = "Get emergency zones in map area",
      description = "Retrieves emergency zones in a specific map area."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Emergency zones retrieved successfully."),
      @ApiResponse(responseCode = "404", description = "No emergency zones found in the specified area."),
      @ApiResponse(responseCode = "400", description = "Invalid request parameters."),
      @ApiResponse(responseCode = "500", description = "Internal server error.")
  })
  public ResponseEntity<?> getZonesInMapArea(
      @RequestBody QueryRequestInArea request) {
    try {
      if (request.getMapArea() == null || request.getMapArea().isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Error: Map area cannot be null or empty.");
      }

      List<ZoneFullDTO> emergencyZones = mapEntityService.getMapZonesInMapArea(
          request.getMapArea(),
          request.getExcludedIds().toArray(new Long[0]));

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
              schema = @Schema(implementation = ZoneFullDTO.class)
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
          example = "1",
          required = true
      ) @PathVariable Long zoneId) {
    try {
      ZoneFullDTO emergencyZone = mapEntityService.getMapZoneById(zoneId);
      return ResponseEntity.ok(emergencyZone);
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
  @PostMapping("/zone/create")
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
              schema = @Schema(implementation = ZoneCreateDTO.class)
          )
      ) @RequestBody ZoneCreateDTO zone) {
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
          example = "1",
          required = true
      ) @PathVariable Long zoneId,
      @Parameter(
          description = "The updated emergency zone data.",
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ZoneCreateDTO.class)
          )
      ) @RequestBody ZoneCreateDTO zone) {
    try {
      mapEntityService.updateZone(zoneId, zone);
      return ResponseEntity.ok("Zone updated successfully.");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    }
  }

  /**
   * Endpoint for retrieving all markers from the database.
   *
   * @return A list of all markers.
   */
  @GetMapping("/markers")
  @Operation(
      summary = "Get all markers",
      description = "Retrieves all markers from the database."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Markers retrieved successfully.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = MarkerFullDTO.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "No markers found.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: No markers found.")
          )
      )
  })
  public ResponseEntity<?> getMarkers() {
    List<MarkerFullDTO> markers = mapEntityService.getAllMapMarkers();
    if (markers.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: No markers found.");
    }
    return ResponseEntity.ok(markers);
  }

  /**
   * Endpoint for retrieving markers in a specific map area.
   *
   * @param request Contains map area and excluded marker IDs
   * @return A list of markers in the specified map area.
   */
  @PostMapping("/markers/in-area")
  @Operation(
      summary = "Get markers in map area",
      description = "Retrieves markers in a specific map area."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Markers retrieved successfully."),
      @ApiResponse(responseCode = "404", description = "No markers found in the specified area."),
      @ApiResponse(responseCode = "400", description = "Invalid request parameters."),
      @ApiResponse(responseCode = "500", description = "Internal server error.")
  })
  public ResponseEntity<?> getMarkersInMapArea(
      @RequestBody QueryRequestInArea request) {
    try {
      if (request.getMapArea() == null || request.getMapArea().isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Error: Map area cannot be null or empty.");
      }

      List<MarkerFullDTO> markers = mapEntityService.getMapMarkersInMapArea(
          request.getMapArea(),
          request.getExcludedIds().toArray(new Long[0]));

      if (markers.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("Error: No markers found in the specified area.");
      }
      return ResponseEntity.ok(markers);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Error: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error: An unexpected error occurred. " + e.getMessage());
    }
  }

  /**
   * Endpoint for retrieving a specific marker by its ID.
   *
   * @param markerId The ID of the marker to retrieve.
   * @return The marker with the specified ID.
   */
  @GetMapping("/marker/{markerId}")
  @Operation(
      summary = "Get marker by ID",
      description = "Retrieves a specific marker by its ID."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Marker retrieved successfully.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = MarkerFullDTO.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Marker not found.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Marker not found.")
          )
      )
  })
  public ResponseEntity<?> getMarkerById(
      @Parameter(
          description = "The ID of the marker to retrieve.",
          example = "1",
          required = true
      ) @PathVariable Long markerId) {
    try {
      MarkerFullDTO emergencyZone = mapEntityService.getMapMarkerById(markerId);
      return ResponseEntity.ok(emergencyZone);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Marker not found." + e.getMessage());
    }
  }

  /**
   * Endpoint for creating a new marker.
   *
   * @param marker The marker to create.
   * @return The ID of the newly created marker.
   */
  @PostMapping("/marker/create")
  @Operation(
      summary = "Create a new marker",
      description = "Creates a new marker."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "Marker created successfully.",
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
  public ResponseEntity<?> createMarker(
      @Parameter(
          description = "The marker to create.",
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = MarkerCreateDTO.class)
          )
      ) @RequestBody MarkerCreateDTO marker) {
    try {
      Long markerId = mapEntityService.createMarker(marker);
      return ResponseEntity.status(HttpStatus.CREATED).body(markerId);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    }
  }

  /**
   * Endpoint for updating an existing marker
   *
   * @param markerId The ID of the marker to update.
   * @param marker The updated marker data.
   * @return A success message.
   */
  @PutMapping("/marker/update/{markerId}")
  @Operation(
      summary = "Update an existing marker",
      description = "Updates an existing marker."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Marker updated successfully.",
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
  public ResponseEntity<?> updateMarker(
      @Parameter(
          description = "The ID of the marker to update.",
          example = "1",
          required = true
      ) @PathVariable Long markerId,
      @Parameter(
          description = "The updated marker data.",
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = MarkerCreateDTO.class)
          )
      ) @RequestBody MarkerCreateDTO marker) {
    try {
      mapEntityService.updateMarker(markerId, marker);
      return ResponseEntity.ok("Marker updated successfully.");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    }
  }

  /**
   * Endpoint for retrieving the coordinates of a specific map entity by its ID.
   *
   * @param id The ID of the map entity to retrieve the coordinates from.
   * @return The coordinates of the map entity with the specified ID.
   */
  @GetMapping("/coordinates/{id}")
  @Operation(
      summary = "Get map entity coordinates by ID",
      description = "Retrieves the coordinates of a specific map entity by its ID."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Emergency zone coordinates retrieved successfully.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = CoordinatesDTO.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Map entity not found.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Map Entity not found.")
          )
      )
  })
  public ResponseEntity<?> getMapEntityCoordinates(
      @Parameter(
          description = "The ID of the map entity to retrieve the coordinates from.",
          example = "1",
          required = true
      ) @PathVariable Long id) {
    try {
      CoordinatesDTO coordinates = mapEntityService.getMapEntityCoordinates(id);
      return ResponseEntity.ok(coordinates);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Map Entity not found." + e.getMessage());
    }
  }

  /**
   * Endpoint for retrieving the description of a specific map entity by its ID.
   *
   * @param id The ID of the map entity to retrieve the description from.
   * @return The description of the map entity with the specified ID.
   */
  @GetMapping("/description/{id}")
  @Operation(
      summary = "Get map entity description by ID",
      description = "Retrieves the description of a specific map entity by its ID."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Emergency zone description retrieved successfully.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = MapEntityDescDTO.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Map entity not found.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Map Entity not found.")
          )
      )
  })
  public ResponseEntity<?> getDescription(
      @Parameter(
          description = "The ID of the map entity to retrieve the description from.",
          example = "1",
          required = true
      ) @PathVariable Long id) {
    try {
      MapEntityDescDTO mapEntityDesc = mapEntityService.getMapEntityDescById(id);
      return ResponseEntity.ok(mapEntityDesc);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Map Entity not found." + e.getMessage());
    }
  }

  /**
   * Endpoint for deleting a map entity by its ID.
   *
   * @param id The ID of the map entity to delete.
   * @return A success message.
   */
  @DeleteMapping("/delete/{id}")
  @Operation(
      summary = "Delete a map entity by ID",
      description = "Deletes a map entity by its ID."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Map entity deleted successfully.",
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
  public ResponseEntity<?> deleteEntity(
      @Parameter(
          description = "The ID of the entity to delete.",
          example = "1",
          required = true
      ) @PathVariable Long id) {
    try {
      mapEntityService.deleteMapEntity(id);
      return ResponseEntity.ok("Entity deleted successfully.");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    }
  }

  /**
   * Endpoint for retrieving all zone types as a list of strings.
   *
   * @return A list of all zone types.
   */
  @GetMapping("/zone-types")
  @Operation(
      summary = "Get all zone types",
      description = "Retrieves all zone types as a list of strings."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Zone types retrieved successfully.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = String.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "No zone types found.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: No zone types found.")
          )
      )
  })
  public ResponseEntity<?> getAllZoneTypes() {
    List<TypeFullDTO> zoneTypes = mapEntityService.getZoneTypes();
    if (zoneTypes.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: No zone types found.");
    }
    return ResponseEntity.ok(zoneTypes);
  }

  /**
   * Endpoint for retrieving all marker types as a list of strings.
   *
   * @return A list of all marker types.
   */
  @GetMapping("/marker-types")
  @Operation(
      summary = "Get all marker types",
      description = "Retrieves all marker types as a list of strings."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Marker types retrieved successfully.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = String.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "No marker types found.",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: No marker types found.")
          )
      )
  })
  public ResponseEntity<?> getAllMarkerTypes() {
    List<TypeFullDTO> markerTypes = mapEntityService.getMarkerTypes();
    if (markerTypes.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: No marker types found.");
    }
    return ResponseEntity.ok(markerTypes);
  }
}
