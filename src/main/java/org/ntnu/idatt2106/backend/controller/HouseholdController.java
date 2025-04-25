package org.ntnu.idatt2106.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.ntnu.idatt2106.backend.dto.household.PreparednessStatus;
import org.ntnu.idatt2106.backend.model.Household;
import org.ntnu.idatt2106.backend.repo.HouseholdRepo;
import org.ntnu.idatt2106.backend.service.PreparednessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
                    content = @Content(schema = @Schema(implementation = PreparednessStatus.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Household not found",
                    content = @Content(schema = @Schema(example = "Household not found"))
            )
    })
    public ResponseEntity<PreparednessStatus> getPreparednessStatus(@PathVariable int id) {
        Household household = householdRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Household not found"));

        PreparednessStatus status = preparednessService.calculatePreparednessStatus(household);
        return ResponseEntity.ok(status);
    }
}
