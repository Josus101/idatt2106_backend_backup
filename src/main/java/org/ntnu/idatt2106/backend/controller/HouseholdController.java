package org.ntnu.idatt2106.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.ntnu.idatt2106.backend.dto.household.PreparednessStatus;
import org.ntnu.idatt2106.backend.repo.HouseholdRepo;
import org.ntnu.idatt2106.backend.service.PreparednessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.NoSuchElementException;

/**
 * Controller class for handling household-related operations.
 * This class is responsible for defining the endpoints for household preparedness status retrieval.
 *
 * @author Erlend Eide Zindel
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/households")
public class HouseholdController {
    @Autowired
    private HouseholdRepo householdRepo;

    @Autowired
    private PreparednessService preparednessService;

    /**
     * Endpoint for calculating the preparedness status of a household.
     *
     * @param id The ID of the household.
     * @return A PreparednessStatus object with percentage and warning info.
     */
    @GetMapping("/{id}/preparedness")
    @Operation(
            summary = "Get preparedness status",
            description = "Returns the preparedness level of the given household based on food, water and essential items"
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200",
                description = "Preparedness status successfully retrieved",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PreparednessStatus.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Household not found",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "Error: Household not found"))
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(
                    mediaType = "application/json",
                    schema =  @Schema(example = "Error: Could not fetch preparedness status"))
            )
    })
    public ResponseEntity<?> getPreparednessStatus(
            @Parameter(
                description = "ID of the household to retrieve preparedness status for",
                required = true,
                example = "1"
            ) @PathVariable int id
    ){
        try {
            PreparednessStatus status = preparednessService.getPreparednessStatusByHouseholdId(id);
            return ResponseEntity.ok(status);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Household not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Could not fetch preparedness status");
        }
    }
}
