package org.ntnu.idatt2106.backend.controller;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ntnu.idatt2106.backend.dto.category.CategoryGetResponse;
import org.ntnu.idatt2106.backend.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
            "Test Category"
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

}