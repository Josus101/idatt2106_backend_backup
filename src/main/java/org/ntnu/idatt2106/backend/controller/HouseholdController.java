package org.ntnu.idatt2106.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.ntnu.idatt2106.backend.dto.household.HouseholdCreate;
import org.ntnu.idatt2106.backend.dto.household.EssentialItemStatusDTO;
import org.ntnu.idatt2106.backend.dto.household.PreparednessStatus;
import org.ntnu.idatt2106.backend.exceptions.UnauthorizedException;
import org.ntnu.idatt2106.backend.model.Household;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.repo.HouseholdRepo;
import org.ntnu.idatt2106.backend.security.JWT_token;
import org.ntnu.idatt2106.backend.service.EssentialItemService;
import org.ntnu.idatt2106.backend.security.JWT_token;
import org.ntnu.idatt2106.backend.service.HouseholdService;
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
 * @author Erlend Eide Zindel, Konrad Seime
 * @version 0.2
 * @since 0.1
 */
@RestController
@RequestMapping("/api/households")
public class HouseholdController {

    @Autowired
    private HouseholdService householdService;

    @Autowired
    private HouseholdRepo householdRepo;

    @Autowired
    private PreparednessService preparednessService;

    @Autowired
    private EssentialItemService essentialItemService;

    @Autowired
    private JWT_token jwtTokenService;



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
            int userId = Integer.parseInt(jwtTokenService.extractIdFromJwt(token));
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
            int userId = Integer.parseInt(jwtTokenService.extractIdFromJwt(token));

            var statusList = essentialItemService.getEssentialItemStatusByUserId(userId);
            return ResponseEntity.ok(statusList);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Endpoint for creating a household.
     *
     * @param household The household object to be created.
     */
    @PostMapping("/create")
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
                    content = @Content(schema = @Schema(example = "Error: Invalid household details"))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(example = "Error: Unauthorized"))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(example = "Error: Unexpected error: <error message>"))
            ),
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Unauthorized");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
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
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(example = "Error: Unauthorized"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Household not found",
                    content = @Content(schema = @Schema(example = "Error: Household not found"))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(example = "Error: Unexpected error: <error message>"))
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Unauthorized");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
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
                    content = @Content(schema = @Schema(example = "Error: Invalid join code"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Household not found",
                    content = @Content(schema = @Schema(example = "Error: Household not found"))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(example = "Error: Unexpected error: <error message>"))
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid join code");

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
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
                    content = @Content(schema = @Schema(example = "Error: Invalid household ID"))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not a member of this household",
                    content = @Content(schema = @Schema(example = "Error: User not a member of this household"))
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
                    householdService.leaveHousehold(id, user);
                    return ResponseEntity.ok("Successfully left the household");
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: User not found");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: User not found");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
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
            content = @Content(schema = @Schema(example = "Error: Invalid household ID or user ID"))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "User not authorized to kick this user from the household",
            content = @Content(schema = @Schema(example = "Error: User not authorized to kick this user from the household"))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Household or user not found",
            content = @Content(schema = @Schema(example = "Error: Household or user not found"))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(example = "Unexpected error: <error message>"))
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
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: User not found from token");
                }
                householdService.kickUserFromHousehold(householdId, userId, user);
                return ResponseEntity.ok("User successfully kicked from the household");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: User not authorized to kick this user from the household");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Endpoint for finding a users households based on token
     *
     * @param authorizationHeader The authorization header containing the JWT token.
     */
    @GetMapping("/myHouseholds")
    @Operation(
        summary = "Get all households for a user",
        description = "Returns all households for the user based on the token"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Households successfully retrieved"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No households found for this user",
            content = @Content(schema = @Schema(example = "Error: No households found for this user"))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(example = "Error: Unauthorized"))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(example = "Unexpected error: <error message>"))
        )
    })
    public ResponseEntity<?> getMyHouses(
        @Parameter(
            name = "Authorization",
            description = "Bearer token in the format Bearer <JWT>",
            required = true,
            example = "Bearer eyJhbGciOiJIUzI1N.iIsInR5cCI6IkpXVCJ9..."
        ) @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                User user = jwtTokenService.getUserByToken(token);
                if (user != null) {
                    return ResponseEntity.ok(householdService.getHouseholdsByUser(user));
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Unauthorized");
        } catch (NoSuchElementException e) {
            System.out.println("message: "+e.getMessage());
            System.out.println("cause: "+e.getCause());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("message: "+e.getMessage());
            System.out.println("cause: "+e.getCause());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }


}
