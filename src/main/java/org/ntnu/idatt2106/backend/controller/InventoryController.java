package org.ntnu.idatt2106.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.ntnu.idatt2106.backend.dto.item.ItemCreateRequest;
import org.ntnu.idatt2106.backend.dto.item.ItemGenericDTO;
import org.ntnu.idatt2106.backend.exceptions.UserNotFoundException;
import org.ntnu.idatt2106.backend.security.JWT_token;
import org.ntnu.idatt2106.backend.service.CategoryService;
import org.ntnu.idatt2106.backend.service.ItemService;
import org.ntnu.idatt2106.backend.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling inventory-related operations.
 * This class is responsible for defining the endpoints for managing items in the inventory.
 *
 * @version 0.2
 * @since 0.1
 * @author Jonas Reiher, Eskild Smestu
 */
@RestController
@RequestMapping("/api/emergency/items")
public class InventoryController {

  // Service classes
  @Autowired
  ItemService itemService;

  @Autowired
  UnitService unitService;

  @Autowired
  CategoryService categoryService;

  @Autowired
  JWT_token jwtToken;


  // Why is there non endpoint method in this controller class?
  /**
   * Helper method to extract user ID from request
   *
   * @param authorizationHeader the Authorization header containing the JWT token
   * @return the user ID extracted from the token
   * @throws IllegalArgumentException if the token is invalid
   */
  private int getUserIdFromRequest(String authorizationHeader) throws IllegalArgumentException {
    try {
      String token = authorizationHeader.substring(7);
      return jwtToken.getUserByToken(token).getId();
    } catch (IllegalArgumentException | UserNotFoundException e) {
      throw new IllegalArgumentException("Invalid token");
    }
  }

  //Todo: Remove this endpoint, as it gives all items regardless of household
  /**
   * Endpoint for retrieving all items in the inventory.
   *
   * @return a response entity containing the list of items
   */
  @GetMapping("")
  @Operation(
      summary = "Get all items",
      description = "Endpoint for retrieving all items in the inventory"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Items retrieved successfully",
          content = @Content(
              schema = @Schema(implementation = ItemGenericDTO.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "No items found",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: No items found")
          )
      )
  })
  public ResponseEntity<?> getInventory() {
    try {
      return ResponseEntity.ok(itemService.getAllItems());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error: " + e.getMessage());
    }
  }

  /**
   * Endpoint for retrieving an item by its ID.
   * @param id the ID of the item to be retrieved
   * @param authorizationHeader the Authorization header containing the JWT token
   * @return a response entity containing the item details
   */
  @GetMapping("/{id}")
  @Operation(
      summary = "Get an item",
      description = "Endpoint for retrieving an item by its ID"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Item retrieved successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ItemGenericDTO.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Item not found",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Item not found")
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Unauthorized")
          )
      )
  })
  public ResponseEntity<?> getItem(
      @Parameter(description = "The id of the requested item", example = "1")
      @PathVariable int id,
      @RequestHeader("Authorization") String authorizationHeader) {
    try {
      int userId = getUserIdFromRequest(authorizationHeader);
      return ResponseEntity.ok(itemService.getItemById(id, userId));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Unauthorized");
    }
  }

  /**
   * Endpoint for adding a new item to the inventory.
   * @param itemCreateRequest the request body containing item details
   * @param authorizationHeader the Authorization header containing the JWT token
   * @return a response entity with the status of the operation
   */
  @PostMapping("")
  @Operation(
      summary = "Add an item",
      description = "Endpoint for adding a new item to the inventory"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "Item added successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Item added successfully")
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid item data",
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "Invalid category ID",
                      value = "Error: Category not found"
                  ),
                  @ExampleObject(
                      name = "Invalid unit ID",
                      value = "Error: Unit not found"
                  )
              }
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Unauthorized")
          )
      )
  })
  public ResponseEntity<?> addItem(
      @RequestBody ItemCreateRequest itemCreateRequest,
      @RequestHeader("Authorization") String authorizationHeader) {
    try {
      int userId = getUserIdFromRequest(authorizationHeader);
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(itemService.addItem(itemCreateRequest, userId));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Unauthorized");
    }
  }

  /**
   * Endpoint for updating an existing item in the inventory.
   * @param itemData the request body containing updated item details
   * @param authorizationHeader the Authorization header containing the JWT token
   * @return a response entity with the status of the operation
   */
  @PutMapping("")
  @Operation(
      summary = "Update an item",
      description = "Endpoint for updating an existing item in the inventory"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Item updated successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Item updated successfully")
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Could not find item, category or unit",
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "Item not found",
                      value = "Error: Item not found"
                  ),
                  @ExampleObject(
                      name = "Invalid category ID",
                      value = "Error: Category not found"
                  ),
                  @ExampleObject(
                      name = "Invalid unit ID",
                      value = "Error: Unit not found"
                  )
              }
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid item data",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Invalid item data")
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Unauthorized")
          )
      )
  })
  public ResponseEntity<?> updateItem(
      @RequestBody ItemGenericDTO itemData,
      @RequestHeader("Authorization") String authorizationHeader) {
    try {
      int userId = getUserIdFromRequest(authorizationHeader);
      return ResponseEntity.status(HttpStatus.OK).body(itemService.updateItem(itemData, userId));
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Unauthorized");
    }
  }

  /**
   * Endpoint for deleting an item from the inventory.
   * @param id the ID of the item to be deleted
   * @param authorizationHeader the Authorization header containing the JWT token
   * @return a response entity with the status of the operation
   */
  @DeleteMapping("/{id}")
  @Operation(
      summary = "Deleting an Item",
      description = "Endpoint for deleting an existing item in the inventory"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Item deleted successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Item deleted successfully")
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Item not found",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Item not found")
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Unauthorized")
          )
      )
  })
  public ResponseEntity<String> deleteItem(
      @Parameter(
          description = "The id of the item to be deleted",
          example = "1"
      ) @PathVariable int id,
      @RequestHeader("Authorization") String authorizationHeader
  ){
    try {
      int userId = getUserIdFromRequest(authorizationHeader);
      itemService.deleteItem(id, userId);
      return ResponseEntity.ok("Item deleted successfully");
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Unauthorized");
    }
  }

  /**
   * Endpoint for retrieving all items associated with a category.
   * @param id the ID of the category
   * @param householdId the ID of the household
   * @param authorizationHeader the Authorization header containing the JWT token
   * @return a response entity containing the list of items associated with the category
   */
  @GetMapping("/categories/{id}/household/{householdId}")
  @Operation(
      summary = "Get all items associated with a category and household",
      description = "Endpoint for retrieving all items associated with a category and household"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Items retrieved successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ItemGenericDTO.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "No items found for this category",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: No items found for this category")
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid category ID",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Category does not exist")
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Unauthorized")
          )
      )
  })
  public ResponseEntity<?> getItemsByCategory(
      @Parameter(
          description = "The id of the category",
          example = "1"
      ) @PathVariable int id,
      @PathVariable int householdId,
      @RequestHeader("Authorization") String authorizationHeader
  ){
    try {
      int userId = getUserIdFromRequest(authorizationHeader);
      return ResponseEntity.ok(itemService.getItemsByCategoryIdAndHouseholdId(id, householdId, userId));
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Unauthorized");
    }
  }

  /**
   * Endpoint for retrieving all items associated with a household.
   * @param id the ID of the household
   * @param authorizationHeader the Authorization header containing the JWT token
   * @return a response entity containing the list of items associated with the household
   */
  @GetMapping("/household/{id}")
  @Operation(
      summary = "Get all items associated with a household",
      description = "Endpoint for retrieving all items associated with a household"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Items retrieved successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ItemGenericDTO.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "No items found for this household",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: No items found for this household")
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Unauthorized")
          )
      )
  })
  public ResponseEntity<?> getInventoryForHousehold(
      @Parameter(
          description = "The id of the household",
          example = "1")
      @PathVariable int id,
      @RequestHeader("Authorization") String authorizationHeader) {
    try {
      int userId = getUserIdFromRequest(authorizationHeader);
      return ResponseEntity.ok(itemService.getItemsByHouseholdId(id, userId));
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Unauthorized");
    }
  }
}