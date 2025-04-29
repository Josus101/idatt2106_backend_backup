package org.ntnu.idatt2106.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.ntnu.idatt2106.backend.dto.category.CategoryGetResponse;
import org.ntnu.idatt2106.backend.dto.unit.UnitGetResponse;
import org.ntnu.idatt2106.backend.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller class for handling unit-related operations.
 * This class is responsible for defining the endpoints for unit retrieval.
 *
 * @author Jonas Reiher
 * @version 0.1
 * @since 0.1
 */
@RestController
@RequestMapping("/api/units")
public class UnitController {

  @Autowired
  UnitService unitService;

  /**
   * Retrieves all units.
   *
   * @return a list of all units
   */
  @GetMapping("")
  @Operation(
      summary = "Get all units",
      description = "Retrieves all units."
  )
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "Units retrieved successfully",
              content = @Content(
                  mediaType =  "application/json",
                  schema = @Schema(implementation = UnitGetResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "No units found",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(example = "Error: No units found")
              )
          )
      })
  public ResponseEntity<?> getAllUnits() {
    List<UnitGetResponse> units = unitService.getAllUnits();
    if (units.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: No units found");
    }
    return ResponseEntity.status(HttpStatus.OK).body(units);
  }

  /**
   * Retrieves a unit by its ID.
   *
   * @param id the ID of the unit
   * @return the unit with the given ID
   */
  @GetMapping("/{id}")
  @Operation(
      summary = "Get unit by ID",
      description = "Retrieves a unit by its ID."
  )
  @ApiResponses(
      value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "Unit retrieved successfully",
                  content = @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = UnitGetResponse.class)
                  )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Unit not found",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(example = "Error: Unit not found")
              )
          )
      })
  public ResponseEntity<?> getUnitById(
          @Parameter(
                  description = "The id of the unit",
                  example = "1"
          ) @PathVariable int id) {
    try {
      return ResponseEntity.status(HttpStatus.OK).body(unitService.getUnitById(id));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
    }
  }
}
