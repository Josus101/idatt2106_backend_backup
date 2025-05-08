package org.ntnu.idatt2106.backend.controller;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ntnu.idatt2106.backend.dto.category.CategoryCreateRequest;
import org.ntnu.idatt2106.backend.dto.category.CategoryGetResponse;
import org.ntnu.idatt2106.backend.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the CategoryController class.
 */
class CategoryControllerTest {
  @InjectMocks
  private CategoryController categoryController;

  @Mock
  CategoryService categoryService;

  private CategoryGetResponse categoryGetResponse;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    MockMvcBuilders.standaloneSetup(categoryController).build();
    categoryGetResponse = new CategoryGetResponse(
            1,
            "Test Category",
        "Test kategori"
    );
  }

  @Test
  @DisplayName("getCategories method returns success on existing categories")
  void getCategoriesSuccess() {
    List<CategoryGetResponse> categories = List.of(categoryGetResponse);
    when(categoryService.getAllCategories()).thenReturn(categories);

    ResponseEntity<?> response = categoryController.getCategories();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(categories, response.getBody());
  }

  @Test
  @DisplayName("getCategories method returns not found on no existing categories")
  void getCategoriesNotFound() {
    when(categoryService.getAllCategories()).thenReturn(List.of());

    ResponseEntity<?> response = categoryController.getCategories();

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error: No categories found", response.getBody());
  }

  @Test
  @DisplayName("getCategoryById method returns success on existing category")
  void getCategoryByIdSuccess() {
    when(categoryService.getCategoryById(1)).thenReturn(categoryGetResponse);

    ResponseEntity<?> response = categoryController.getCategoryById(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(categoryGetResponse, response.getBody());
  }

  @Test
  @DisplayName("getCategoryById method returns not found on non-existing category")
  void getCategoryByIdNotFound() {
    when(categoryService.getCategoryById(1)).thenThrow(new EntityNotFoundException("Category not found"));

    ResponseEntity<?> response = categoryController.getCategoryById(1);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error: Category not found", response.getBody());
  }

  @Test
  @DisplayName("addCategory returns CREATED on valid input")
  void addCategorySuccess() {
    CategoryCreateRequest request = new CategoryCreateRequest("New Category", "Ny kategoriiiiii", 100, true);
    String token = "Bearer valid-token";

    ResponseEntity<?> response = categoryController.addCategory(request, token);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("Category added successfully", response.getBody());
  }

  @Test
  @DisplayName("addCategory returns BAD_REQUEST on invalid input")
  void addCategoryBadRequest() {
    CategoryCreateRequest request = new CategoryCreateRequest("", "", -10, true);
    String token = "Bearer invalid-token";

    doThrow(new IllegalArgumentException("Invalid category data")).when(categoryService).createCategory(request, token);

    ResponseEntity<?> response = categoryController.addCategory(request, token);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Invalid category data", response.getBody());
  }

  @Test
  @DisplayName("addCategory returns INTERNAL_SERVER_ERROR on exception")
  void addCategoryServerError() {
    CategoryCreateRequest request = new CategoryCreateRequest("New Category", "Ny kategoriiiiii", 100, true);
    String token = "Bearer valid-token";

    doThrow(new RuntimeException("Unexpected error")).when(categoryService).createCategory(request, token);

    ResponseEntity<?> response = categoryController.addCategory(request, token);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Error: Unexpected error", response.getBody());
  }

  @Test
  @DisplayName("updateCategory returns OK on success")
  void updateCategorySuccess() {
    CategoryCreateRequest request = new CategoryCreateRequest("Updated Category", "Oppdatert kategoriiiiii", 150, false);
    String token = "Bearer valid-token";

    ResponseEntity<?> response = categoryController.updateCategory(1, request, token);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Category updated successfully", response.getBody());
  }

  @Test
  @DisplayName("updateCategory returns NOT_FOUND when category is missing")
  void updateCategoryNotFound() {
    CategoryCreateRequest request = new CategoryCreateRequest("Updated Category", "Oppdatert kategoriiiiii", 150, false);
    String token = "Bearer valid-token";

    doThrow(new EntityNotFoundException("Category not found")).when(categoryService).updateCategory(1, request, token);

    ResponseEntity<?> response = categoryController.updateCategory(1, request, token);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error: Category not found", response.getBody());
  }

  @Test
  @DisplayName("updateCategory returns BAD_REQUEST on invalid input")
  void updateCategoryBadRequest() {
    CategoryCreateRequest request = new CategoryCreateRequest(null, null, 0, true);
    String token = "Bearer invalid-token";

    doThrow(new IllegalArgumentException("Invalid data")).when(categoryService).updateCategory(1, request, token);

    ResponseEntity<?> response = categoryController.updateCategory(1, request, token);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Invalid data", response.getBody());
  }

  @Test
  @DisplayName("deleteCategory returns OK on success")
  void deleteCategorySuccess() {
    String token = "Bearer valid-token";

    ResponseEntity<?> response = categoryController.deleteCategory(1, token);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Category deleted successfully", response.getBody());
  }

  @Test
  @DisplayName("deleteCategory returns NOT_FOUND when category does not exist")
  void deleteCategoryNotFound() {
    String token = "Bearer valid-token";

    doThrow(new EntityNotFoundException("Category not found")).when(categoryService).deleteCategory(1, token);

    ResponseEntity<?> response = categoryController.deleteCategory(1, token);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error: Category not found", response.getBody());
  }

  @Test
  @DisplayName("deleteCategory returns BAD_REQUEST on bad input")
  void deleteCategoryBadRequest() {
    String token = "Bearer invalid-token";

    doThrow(new IllegalArgumentException("Invalid category ID")).when(categoryService).deleteCategory(1, token);

    ResponseEntity<?> response = categoryController.deleteCategory(1, token);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Invalid category ID", response.getBody());
  }



}