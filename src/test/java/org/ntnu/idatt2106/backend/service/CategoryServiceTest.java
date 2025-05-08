package org.ntnu.idatt2106.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ntnu.idatt2106.backend.dto.category.CategoryGetResponse;
import org.ntnu.idatt2106.backend.model.Category;
import org.ntnu.idatt2106.backend.repo.CategoryRepo;

import java.lang.annotation.Inherited;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class CategoryServiceTest {
  @InjectMocks
  private CategoryService categoryService;

  @Mock
  private CategoryRepo categoryRepo;

  private Category testCategory;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    testCategory = new Category();
    testCategory.setId(1);
    testCategory.setEnglishName("Test Category");
    testCategory.setNorwegianName("Test Kategori");
  }

  @Test
  @DisplayName("getCategoryById should return a CategoryGetResponse when category exists")
  void getCategoryByIdSuccess() {
    when(categoryRepo.findById(1)).thenReturn(java.util.Optional.of(testCategory));

    CategoryGetResponse result = categoryService.getCategoryById(1);

    assertNotNull(result);
    assertEquals(testCategory.getId(), result.getId());
    assertEquals(testCategory.getEnglishName(), result.getEnglishName());
    assertEquals(testCategory.getNorwegianName(), result.getNorwegianName());
  }

  @Test
  @DisplayName("getCategoryById should throw an exception if the category is not found")
  void getCategoryByIdNotFound() {
    when(categoryRepo.findById(1)).thenReturn(java.util.Optional.empty());

    try {
      categoryService.getCategoryById(1);
    } catch (Exception e) {
      assertEquals("Category not found", e.getMessage());
    }
  }

  @Test
  @DisplayName("getAllCategories should return a list of CategoryGetResponse")
  void getAllCategories() {
    when(categoryRepo.findAll()).thenReturn(java.util.List.of(testCategory));

    java.util.List<CategoryGetResponse> result = categoryService.getAllCategories();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testCategory.getId(), result.get(0).getId());
    assertEquals(testCategory.getEnglishName(), result.get(0).getEnglishName());
    assertEquals(testCategory.getNorwegianName(), result.get(0).getNorwegianName());
  }

  @Test
  @DisplayName("getAllCategories should return an empty list if no categories are found")
  void getAllCategoriesEmpty() {
    when(categoryRepo.findAll()).thenReturn(java.util.Collections.emptyList());

    java.util.List<CategoryGetResponse> result = categoryService.getAllCategories();

    assertNotNull(result);
    assertEquals(0, result.size());
  }
}
