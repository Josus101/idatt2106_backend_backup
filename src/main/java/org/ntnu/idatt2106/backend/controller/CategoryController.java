package org.ntnu.idatt2106.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.internal.build.AllowNonPortable;
import org.ntnu.idatt2106.backend.dto.category.CategoryCreateRequest;
import org.ntnu.idatt2106.backend.dto.category.CategoryGetResponse;
import org.ntnu.idatt2106.backend.dto.item.ItemGenericDTO;
import org.ntnu.idatt2106.backend.model.Admin;
import org.ntnu.idatt2106.backend.security.JWT_token;
import org.ntnu.idatt2106.backend.service.CategoryService;
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

import java.util.List;

/**
 * Controller class for handling category-related operations.
 * This class is responsible for defining the endpoints for category retrieval.
 *
 * @author Jonas Reiher
 * @version 0.1
 * @since 0.1
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

  @Autowired
  CategoryService categoryService;

  @Autowired
  JWT_token jwt;

  /**
   * Retrieves a category by its ID.
   *
   * @param id the ID of the category
   * @return the category with the given ID
   */
  @GetMapping("/{id}")
  @Operation(
      summary = "Get category by ID",
      description = "Retrieves a category by its ID."
  )
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "Category retrieved successfully",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = CategoryGetResponse.class)
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "Category not found",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(example = "Error: Category not found")
              )
          )
      })
  public ResponseEntity<?> getCategoryById(
          @Parameter(
                  description = "The id of the category",
                  example = "1"
          ) @PathVariable int id) {
    try {
      return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategoryById(id));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
    }
  }

  /**
   * Endpoint for retrieving all categories.
   * @return a response entity containing the list of categories
   */
  @GetMapping("/categories")
  @Operation(
          summary = "Get all categories",
          description = "Endpoint for retrieving all categories"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Categories retrieved successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = CategoryGetResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "No categories found",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: No categories found")
          )
      )
  })
  public ResponseEntity<?> getCategories() {
    List<CategoryGetResponse> categories = categoryService.getAllCategories();
    if (categories.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: No categories found");
    }
    return ResponseEntity.status(HttpStatus.OK).body(categories);
  }

  /**
   * Add new categories to the database.
   *
   * @param category the category to add
   * @param authorization the authorization token
   * @return a response entity with the status of the operation
   */
  @PostMapping("/add")
  @Operation(
      summary = "Add new category",
      description = "Endpoint for adding new categories to the database"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Category added successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ItemGenericDTO.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Bad request",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Bad request")
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Unauthorized")
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Internal server error")
          )
      )
  })
  public ResponseEntity<?> addCategory(
          @Parameter(
                  description = "The category to add",
                  example = "{ \"name\": \"New Category\", \"kcalPerUnit\": 100, \"isEssential\": true }"
          ) @RequestBody CategoryCreateRequest category,
          @Parameter(
                  description = "The authorization token",
                  example = "Bearer <token>"
          ) @RequestHeader("Authorization") String authorization) {
    try {
      categoryService.createCategory(category, authorization);
      return ResponseEntity.status(HttpStatus.CREATED).body("Category added successfully");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
    }
  }

  /**
   * Updates existing categories in the database.
   *
   * @param id the ID of the category to update
   * @param category the updated category data
   * @param authorization the authorization token
   *  @return a response entity with the status of the operation
   */
  @PutMapping("/update/{id}")
  @Operation(
      summary = "Update existing category",
      description = "Endpoint for updating existing categories in the database"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Category updated successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ItemGenericDTO.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Bad request",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Bad request")
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Unauthorized")
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Category not found",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Category not found")
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Internal server error")
          )
      )
  })
  public ResponseEntity<?> updateCategory(
          @Parameter(
                  description = "The ID of the category to update",
                  example = "1"
          ) @PathVariable int id,
          @Parameter(
                  description = "The updated category data",
                  example = "{ \"name\": \"Updated Category\", \"kcalPerUnit\": 150, \"isEssential\": false }"
          ) @RequestBody CategoryCreateRequest category,
          @Parameter(
                  description = "The authorization token",
                  example = "Bearer <token>"
          ) @RequestHeader("Authorization") String authorization) {
    try {
      categoryService.updateCategory(id, category, authorization);
      return ResponseEntity.status(HttpStatus.OK).body("Category updated successfully");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
    }
  }

  /**
   * Deletes a category by its ID.
   *
   * @param id the ID of the category to delete
   * @param authorization the authorization token
   * @return a response entity with the status of the operation
   */
  @DeleteMapping("/delete/{id}")
  @Operation(
      summary = "Delete category by ID",
      description = "Endpoint for deleting a category by its ID"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Category deleted successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ItemGenericDTO.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Bad request",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Bad request")
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Unauthorized")
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Category not found",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Category not found")
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Internal server error")
          )
      )
  })
  public ResponseEntity<?> deleteCategory(
          @Parameter(
                  description = "The ID of the category to delete",
                  example = "1"
          ) @PathVariable int id,
          @Parameter(
                  description = "The authorization token",
                  example = "Bearer <token>"
          ) @RequestHeader("Authorization") String authorization) {
    try {
      categoryService.deleteCategory(id, authorization);
      return ResponseEntity.status(HttpStatus.OK).body("Category deleted successfully");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
    }
  }


}
