package org.ntnu.idatt2106.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.ntnu.idatt2106.backend.dto.household.EssentialItemStatusDTO;
import org.ntnu.idatt2106.backend.dto.household.PreparednessStatus;
import org.ntnu.idatt2106.backend.repo.HouseholdRepo;
import org.ntnu.idatt2106.backend.security.JWT_token;
import org.ntnu.idatt2106.backend.service.EssentialItemService;
import org.ntnu.idatt2106.backend.service.PreparednessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Controller class for handling household-related operations.
 * This class is responsible for defining the endpoints for household preparedness status retrieval.
 *
 * @author Erlend Eide Zindel
 * @version 0.2
 * @since 0.1
 */
@RestController
@RequestMapping("/api/households")
public class HouseholdController {

    @Autowired
    private PreparednessService preparednessService;

    @Autowired
    private EssentialItemService essentialItemService;

    @Autowired
    private JWT_token jwtToken;

    /**
     * Endpoint for retrieving the number of days the user's household has food and water for.
     *
     * @param authorizationHeader The Authorization header containing the Bearer token.
     * @return A PreparednessStatus object with days of food and water supply.
     */
    @GetMapping("/preparedness")
    @Operation(
            summary = "Get preparedness status for user",
            description = "Returns how many days of food and water the household(s) has in storage"
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
                description = "User or household not found",
                content = @Content(
                mediaType = "application/json",
                schema = @Schema(example = "Error: No households found for user"))
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(
                mediaType = "application/json",
                schema =  @Schema(example = "Unexpected error: <error message>"))
            )
    })
    public ResponseEntity<?> getPreparednessStatus(
            @Parameter(
                    description = "Authorization token (Bearer JWT)",
                    required = true,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6..."
            ) @RequestHeader ("Authorization") String authorizationHeader
    ){
        try {
            String token = authorizationHeader.substring(7);
            int userId = Integer.parseInt(jwtToken.extractIdFromJwt(token));
            List<PreparednessStatus> status = preparednessService.getPreparednessStatusByUserId(userId);
            return ResponseEntity.ok(status);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Endpoint for retrieving the essential item status of all households
     * associated with the authenticated user.
     *
     * @param authorizationHeader The Authorization header containing the JWT token.
     * @return A list of lists, where each inner list contains {@link EssentialItemStatusDTO}
     *         objects representing the presence of essential items in a household.
     */
    @GetMapping("/essential-items")
    @Operation(
            summary = "Get essential items status for user",
            description = "Returns a list of essential items and whether each one is present in the household(s)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Essential items status retrieved",
                    content = @Content(schema = @Schema(implementation = EssentialItemStatusDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Household not found",
                    content = @Content(schema = @Schema(example = "Error: Household not found"))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(example = "Unexpected error: <error message>"))
            )
    })
    public ResponseEntity<?> getEssentialItemsStatus(
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.substring(7);
            int userId = Integer.parseInt(jwtToken.extractIdFromJwt(token));

            var statusList = essentialItemService.getEssentialItemStatusByUserId(userId);
            return ResponseEntity.ok(statusList);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }
}
