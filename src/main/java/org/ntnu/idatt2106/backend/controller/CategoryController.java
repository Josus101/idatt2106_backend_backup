package org.ntnu.idatt2106.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.internal.build.AllowNonPortable;
import org.ntnu.idatt2106.backend.dto.category.CategoryGetResponse;
import org.ntnu.idatt2106.backend.dto.item.ItemGenericDTO;
import org.ntnu.idatt2106.backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
                          description = "Category retrieved successfully"
                  ),
                  @ApiResponse(
                          responseCode = "404",
                          description = "Category not found"
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
                          schema = @Schema(implementation = ItemGenericDTO.class)
                  )
          ),
          @ApiResponse(
                  responseCode = "404",
                  description = "No categories found",
                  content = @Content(
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
}
