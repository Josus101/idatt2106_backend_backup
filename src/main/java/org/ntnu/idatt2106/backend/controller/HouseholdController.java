package org.ntnu.idatt2106.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.ntnu.idatt2106.backend.dto.household.HouseholdCreate;
import org.ntnu.idatt2106.backend.dto.household.PreparednessStatus;
import org.ntnu.idatt2106.backend.exceptions.UnauthorizedException;
import org.ntnu.idatt2106.backend.model.Household;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.repo.HouseholdRepo;
import org.ntnu.idatt2106.backend.security.JWT_token;
import org.ntnu.idatt2106.backend.service.HouseholdService;
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
    private HouseholdService householdService;

    @Autowired
    private PreparednessService preparednessService;

    @Autowired
    private JWT_token jwtTokenService;

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

    /**
     * Endpoint for creating a household.
     *
     * @param household The household object to be created.
     */
    @PostMapping
    @Operation(
            summary = "Create a new household",
            description = "Creates a new household with the given details"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Household successfully created"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid household details",
                    content = @Content(schema = @Schema(example = "Invalid household details"))
            )
    })
    public ResponseEntity<?> createHousehold(
        @Parameter(
            name = "Authorization",
            description = "Bearer token in the format Bearer <JWT>",
            required = true,
            example = "Bearer eyJhbGciOiJIUzI1N.iIsInR5cCI6IkpXVCJ9..."
        ) @RequestHeader("Authorization") String authorizationHeader,
        @Parameter(
            name = "household",
            description = "Household object to be created",
            required = true,
            schema = @Schema(implementation = HouseholdCreate.class)

        ) @RequestBody HouseholdCreate household) {
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                User user = jwtTokenService.getUserByToken(token);
                if (user != null) {
                    Household createdHousehold = householdService.createHousehold(household);
                    householdService.addUserToHousehold(createdHousehold, user, true);
                    return ResponseEntity.status(HttpStatus.CREATED).body("Household successfully created");
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid household details");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not create household");
        }
    }

    /**
     * Endpoint for creating an invitation to a household.
     * Returns the join code for the household.
     *
     * @param id The ID of the household.
     */
    @GetMapping("/{id}/invite")
    @Operation(
            summary = "Create an invite to a household",
            description = "Creates an invite to the household and returns the join code"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Join code successfully created"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Household not found",
                    content = @Content(schema = @Schema(example = "Household not found"))
            )
    })
    public ResponseEntity<?> createInvite(
        @Parameter(
            name = "Authorization",
            description = "Bearer token in the format Bearer <JWT>",
            required = true,
            example = "Bearer eyJhbGciOiJIUzI1N.iIsInR5cCI6IkpXVCJ9..."
        ) @RequestHeader("Authorization") String authorizationHeader,
        @Parameter(
            name = "id",
            description = "ID of the household to create an invite for",
            required = true
        ) @PathVariable int id) {
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                User user = jwtTokenService.getUserByToken(token);
                if (user != null) {
                    Household household = householdRepo.findById(id).orElseThrow();
                    String joinCode = householdService.generateJoinCode(household, user);
                    return ResponseEntity.ok(joinCode);
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Household not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not create invite");
        }
    }

    /**
     * Endpoint for joining a household using a join code.
     *
     * @param authorizationHeader The authorization header containing the JWT token.
     * @param joinCode The join code for the household.
     */
    @PostMapping("/{joinCode}/join")
    @Operation(
            summary = "Join a household using a join code",
            description = "Joins the household using the provided join code"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully joined the household"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid join code",
                    content = @Content(schema = @Schema(example = "Invalid join code"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Household not found",
                    content = @Content(schema = @Schema(example = "Household not found"))
            )
    })
    public ResponseEntity<?> joinHouseHold(
        @Parameter(
            name = "Authorization",
            description = "Bearer token in the format Bearer <JWT>",
            required = true,
            example = "Bearer eyJhbGciOiJIUzI1N.iIsInR5cCI6IkpXVCJ9..."
        ) @RequestHeader("Authorization") String authorizationHeader,
        @Parameter(
            name = "joinCode",
            description = "The join code for the household to join",
            required = true
        ) @PathVariable String joinCode) {
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                User user = jwtTokenService.getUserByToken(token);
                if (user != null) {
                    if (householdService.joinHousehold(joinCode, user) == null) {
                        throw new NoSuchElementException("Household not found");
                    }
                    return ResponseEntity.ok("Successfully joined the household");
                }
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Household not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not join household");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid join code");
    }

    /**
     * Endpoint for leaving a household.
     *
     * @param authorizationHeader The authorization header containing the JWT token.
     * @param id The ID of the household to leave.
     */
    @DeleteMapping("/{id}/leave")
    @Operation(
            summary = "Leave a household",
            description = "Leaves the household with the given ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully left the household"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid household ID",
                    content = @Content(schema = @Schema(example = "Invalid household ID"))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not a member of this household",
                    content = @Content(schema = @Schema(example = "User not a member of this household"))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Household not found",
                content = @Content(schema = @Schema(example = "Household not found"))
            )
    })
    public ResponseEntity<?> getMeOutOfThisHousehold(
        @Parameter(
            name = "Authorization",
            description = "Bearer token in the format Bearer <JWT>",
            required = true,
            example = "Bearer eyJhbGciOiJIUzI1N.iIsInR5cCI6IkpXVCJ9..."
        ) @RequestHeader("Authorization") String authorizationHeader,
        @Parameter(
            name = "id",
            description = "The id of the household to leave",
            required = true
        ) @PathVariable int id) {
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                User user = jwtTokenService.getUserByToken(token);
                if (user != null) {
                    System.out.println("User from token: " + user);
                    System.out.println("Leaving household with ID: " + id);
                    System.out.println("Authorization header: " + authorizationHeader);
                    householdService.leaveHousehold(id, user);
                    return ResponseEntity.ok("Successfully left the household");
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid household ID");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Household not found");
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not a member of this household");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not leave household");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not leave household");
    }

    /**
     * Endpoint for kicking a user from a household.
     *
     * @param authorizationHeader The authorization header containing the JWT token.
     * @param userId The ID of the user to be kicked.
     * @param householdId The ID of the household from which the user will be kicked.
     */
    @DeleteMapping("/{householdId}/kick/{userId}")
    @Operation(
        summary = "Kicks a user from a household",
        description = "Kicks the user with the given ID from the household with the given ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User successfully kicked from the household"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid household ID or user ID",
            content = @Content(schema = @Schema(example = "Invalid household ID or user ID"))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "User not authorized to kick this user from the household",
            content = @Content(schema = @Schema(example = "User not authorized to kick this user from the household"))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Household or user not found",
            content = @Content(schema = @Schema(example = "Household or user not found"))
        )
    })
    public ResponseEntity<?> getOutOfMyHouse(
        @Parameter(
            name = "Authorization",
            description = "Bearer token in the format Bearer <JWT>",
            required = true,
            example = "Bearer eyJhbGciOiJIUzI1N.iIsInR5cCI6IkpXVCJ9..."
        ) @RequestHeader("Authorization") String authorizationHeader,
        @Parameter(
            name = "householdId",
            description = "The id of the household to kick someone from",
            required = true
        ) @PathVariable int householdId,
        @Parameter(
            name = "userId",
            description = "The id of the user to kick from the household",
            required = true
        ) @PathVariable int userId) {
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                User user = jwtTokenService.getUserByToken(token);
                if (user == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found from token");
                }
                householdService.kickUserFromHousehold(householdId, userId, user);
                return ResponseEntity.ok("User successfully kicked from the household");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid household ID or user ID");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Household or user not found");
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized to kick this user from the household");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not kick user from household");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized to kick this user from the household");
    }
}
