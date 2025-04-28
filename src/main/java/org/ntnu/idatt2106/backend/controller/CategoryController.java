package org.ntnu.idatt2106.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.hibernate.internal.build.AllowNonPortable;
import org.ntnu.idatt2106.backend.dto.category.CategoryGetResponse;
import org.ntnu.idatt2106.backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseEntity<CategoryGetResponse> getCategoryById(
          @Parameter(
                  description = "The id of the category",
                  example = "1"
          ) @PathVariable int id) {
    CategoryGetResponse category = categoryService.getCategoryById(id);
    return ResponseEntity.ok(category);
  }
}
