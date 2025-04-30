package org.ntnu.idatt2106.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EntityNotFoundException;
import org.ntnu.idatt2106.backend.dto.category.CategoryGetResponse;
import org.ntnu.idatt2106.backend.dto.item.ItemCreateRequest;
import org.ntnu.idatt2106.backend.dto.item.ItemGenericDTO;
import org.ntnu.idatt2106.backend.dto.unit.UnitGetResponse;
import org.ntnu.idatt2106.backend.service.CategoryService;
import org.ntnu.idatt2106.backend.service.ItemService;
import org.ntnu.idatt2106.backend.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

  @Autowired
  UnitService unitService;

  @Autowired
  CategoryService categoryService;

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
  public ResponseEntity<?> addItem(@RequestBody ItemCreateRequest itemCreateRequest) {
    try {
      return ResponseEntity.status(HttpStatus.CREATED).body(itemService.addItem(itemCreateRequest));

    } catch (EntityNotFoundException e) {
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
      )
  })
  public ResponseEntity<?> updateItem(@RequestBody ItemGenericDTO itemData) {
    try {
      return ResponseEntity.status(HttpStatus.OK).body(itemService.updateItem(itemData));

    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());

    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
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
      )
  })
  public ResponseEntity<String> deleteItem(
      @Parameter(
          description = "The id of the item to be deleted",
          example = "1"
      ) @PathVariable int id
  ){
    try {
      itemService.deleteItem(id);
      return ResponseEntity.ok("Item deleted successfully");

    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
    }
  }

  /**
   * Endpoint for retrieving all items associated with a category.
   * @param id the ID of the category
   * @return a response entity containing the list of items associated with the category
   */
  @GetMapping("/categories/{id}")
  @Operation(
      summary = "Get all items associated with a category",
      description = "Endpoint for retrieving all items associated with a category"
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
      )
  })
  public ResponseEntity<?> getItemsByCategory(
      @Parameter(
          description = "The id of the category@PathVariable String id",
          example = "1"
      ) @PathVariable int id
  )
  {
    try {
      return ResponseEntity.ok(itemService.getItemsByCategoryId(id));

    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    }
  }

  // TODO: Maybe move to household controller
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
