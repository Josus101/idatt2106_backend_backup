package org.ntnu.idatt2106.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.ntnu.idatt2106.backend.dto.item.ItemCreateRequest;
import org.ntnu.idatt2106.backend.dto.item.ItemGenericDTO;
import org.ntnu.idatt2106.backend.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// TODO: Update java doc when endpoints are implemented , change version to 1.0
/**
 * Controller class for handling inventory-related operations.
 * This class is responsible for defining the endpoints for managing items in the inventory.
 *
 * @version 1.0
 * @since 1.0
 * @Author Jonas Reiher
 */
@RestController
@RequestMapping("/api/emergency/items")
public class InventoryController {

  // TODO: Implement missing endpoints
  // TODO: Implement authentication check for all endpoints requiring authentication using the
  //  @Parameter(
  //          name = "Authorization",
  //          description = "Bearer token in the format `Bearer <JWT>`",
  //          required = true,
  //          example = "Bearer eyJhbGciOiJIUzI1N.iIsInR5cCI6IkpXVCJ9..."
  //      ) @RequestHeader("Authorization") String authorizationHeader



  // Service classes
  @Autowired
  ItemService itemService;

//  @GetMapping("")
//  @Operation(
//      summary = "Get all items",
//      description = "Endpoint for retrieving all items in the inventory"
//  )
//  public String getInventory() {
//    return "Inventory";
//  }

  /**
   * Endpoint for retrieving an item by its ID.
   * @param id the ID of the item to be retrieved
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
              schema = @Schema(implementation = ItemGenericDTO.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Item not found",
          content = @Content(
              schema = @Schema(example = "Error: Item not found")
          )
      )
  })
  public ResponseEntity<?> getItem(@Parameter(
          description = "The id of the requested item",
          example = "1") @PathVariable int id) {
    try {
      return ResponseEntity.ok(itemService.getItemById(id));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
    }
  }

  /**
   * Endpoint for adding a new item to the inventory.
   * @param itemCreateRequest the request body containing item details
   * @return a response entity with the status of the operation
   */
  @PostMapping("")
  @Operation(
      summary = "Add an item",
      description = "Endpoint for adding a new item to the inventory"
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "Item added successfully",
                  content = @Content(
                          schema = @Schema(example = "Item added successfully")
                  )
          ),
          @ApiResponse(
                  responseCode = "400",
                  description = "Invalid item data",
                  content = @Content(
                          schema = @Schema(example = "Invalid item data")
                  )
          ),
          @ApiResponse(
                  responseCode = "401",
                  description = "Unauthorized",
                  content = @Content(
                          schema = @Schema(example = "Unauthorized")
                  )
          )
  })
  public ResponseEntity<?> addItem(@RequestBody ItemCreateRequest itemCreateRequest) {
    try {
      itemService.addItem(itemCreateRequest);
      return ResponseEntity.ok("Item added successfully");

    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    }
  }

  /**
   * Endpoint for updating an existing item in the inventory.
   * @param itemData the request body containing updated item details
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
              schema = @Schema(example = "Item updated successfully")
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Item not found",
          content = @Content(
              schema = @Schema(example = "Item not found")
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid item data",
          content = @Content(
              schema = @Schema(example = "Invalid item data")
          )
      )
  })
  public ResponseEntity<?> updateItem(@RequestBody ItemGenericDTO itemData) {
    try {
      itemService.updateItem(itemData);
      return ResponseEntity.ok("Item updated successfully");

    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");

    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid item data");
    }
  }

  /**
   * Endpoint for deleting an item from the inventory.
   * @param id the ID of the item to be deleted
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
                      schema = @Schema(example = "Item deleted successfully")
                  )
          ),
          @ApiResponse(
                  responseCode = "404",
                  description = "Item not found",
                  content = @Content(
                      schema = @Schema(example = "Item not found")
                  )
          )
  })
  public ResponseEntity<?> deleteItem(
          @Parameter(
                  description = "The id of the item to be deleted",
                  example = "1") @PathVariable int id
  )
  {
    try {
      itemService.deleteItem(id);
      return ResponseEntity.ok("Item deleted successfully");

    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
    }
  }


//  @GetMapping("/category/{id}")
//  public String getItemsByCategory(@PathVariable String id) {
//    return "Items in category with ID: " + id;
//  }
//
//  @GetMapping("/categories")
//  public String getCategories() {
//    return "Categories";
//  }
//
//  @GetMapping("/units")
//  public String getUnits() {
//    return "Units";
//  }


  // TODO: Maybe move
  /**
   * Endpoint for retrieving all items associated with a household.
   * @param id the ID of the household
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
                          schema = @Schema(implementation = ItemGenericDTO.class)
                  )
          ),
          @ApiResponse(
                  responseCode = "404",
                  description = "No items found for this household",
                  content = @Content(
                          schema = @Schema(example = "Error: No items found for this household")
                  )
          )
  })
  public ResponseEntity<?> getInventoryForHousehold(
          @Parameter(
                  description = "The id of the household",
                  example = "1") @PathVariable int id) {
    try {
      return ResponseEntity.ok(itemService.getItemsByHouseholdId(id));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
    }
  }






}
