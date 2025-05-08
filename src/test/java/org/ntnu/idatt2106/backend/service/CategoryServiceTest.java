package org.ntnu.idatt2106.backend.service;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.ntnu.idatt2106.backend.dto.category.CategoryCreateRequest;
import org.ntnu.idatt2106.backend.dto.category.CategoryGetResponse;
import org.ntnu.idatt2106.backend.model.Admin;
import org.ntnu.idatt2106.backend.model.Category;
import org.ntnu.idatt2106.backend.repo.CategoryRepo;
import org.ntnu.idatt2106.backend.repo.ItemRepo;
import org.ntnu.idatt2106.backend.security.JWT_token;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.times;

public class CategoryServiceTest {
  @InjectMocks
  private CategoryService categoryService;

  @Mock
  private CategoryRepo categoryRepo;

  @Mock
  private JWT_token jwt;

  @Mock
  private ItemRepo itemRepo;

  private Category testCategory;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    testCategory = new Category();
    testCategory.setId(1);
    testCategory.setEnglishName("Test Category");
    testCategory.setNorwegianName("Test Kategori");
    Admin testAdmin = new Admin();
    when(jwt.getAdminUserByToken("valid-token")).thenReturn(testAdmin);
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

  @Test
  @DisplayName("isAdmin returns true for valid admin token")
  void isAdminValidToken() {
    boolean result = categoryService.isAdmin("Bearer valid-token");
    assertTrue(result);
  }

  @Test
  @DisplayName("isAdmin returns false for null token")
  void isAdminNullToken() {
    boolean result = categoryService.isAdmin(null);
    assertFalse(result);
  }

  @Test
  @DisplayName("isAdmin returns false for non-Bearer token")
  void isAdminInvalidPrefix() {
    boolean result = categoryService.isAdmin("Token invalid");
    assertFalse(result);
  }

  @Test
  @DisplayName("isAdmin returns false for empty token")
  void isAdminEmptyToken() {
    boolean result = categoryService.isAdmin("Bearer ");
    assertFalse(result);
  }

  @Test
  @DisplayName("isAdmin returns false if no admin found by token")
  void isAdminNoAdmin() {
    when(jwt.getAdminUserByToken("no-admin")).thenReturn(null);
    boolean result = categoryService.isAdmin("Bearer no-admin");
    assertFalse(result);
  }

  @Test
  @DisplayName("createCategory succeeds with valid input")
  void createCategorySuccess() {
    CategoryCreateRequest request = new CategoryCreateRequest("Monkeyhair", "Apehår", 100, true);
    when(categoryRepo.findByEnglishName("Monkeyhair")).thenReturn(java.util.Optional.empty());

    assertDoesNotThrow(() -> categoryService.createCategory(request, "Bearer valid-token"));
    verify(categoryRepo).save(any(Category.class));
  }

  @Test
  @DisplayName("createCategory throws if not admin")
  void createCategoryUnauthorized() {
    assertThrows(IllegalArgumentException.class, () ->
        categoryService.createCategory(new CategoryCreateRequest("fish", "fisk", 50, true), "Bearer invalid")
    );
  }

  @Test
  @DisplayName("createCategory throws on empty name")
  void createCategoryEmptyName() {
    CategoryCreateRequest request = new CategoryCreateRequest("", "", 50, true);
    assertThrows(IllegalArgumentException.class, () ->
        categoryService.createCategory(request, "Bearer valid-token")
    );
  }

  @Test
  @DisplayName("createCategory throws on negative kcal")
  void createCategoryNegativeKcal() {
    CategoryCreateRequest request = new CategoryCreateRequest("tapeworm", "orm", -50, true);
    assertThrows(IllegalArgumentException.class, () ->
        categoryService.createCategory(request, "Bearer valid-token")
    );
  }

  @Test
  @DisplayName("createCategory throws if name exists")
  void createCategoryDuplicate() {
    CategoryCreateRequest request = new CategoryCreateRequest("TakenOne", "tatten", 100, true);
    when(categoryRepo.findByEnglishName("TakenOne")).thenReturn(java.util.Optional.of(testCategory));

    assertThrows(IllegalArgumentException.class, () ->
        categoryService.createCategory(request, "Bearer valid-token")
    );
  }

  @Test
  @DisplayName("updateCategory succeeds with valid input")
  void updateCategorySuccess() {
    CategoryCreateRequest request = new CategoryCreateRequest("Updated", "Oppdatert", 150, false);
    when(categoryRepo.findById(1)).thenReturn(java.util.Optional.of(testCategory));

    assertDoesNotThrow(() -> categoryService.updateCategory(1, request, "Bearer valid-token"));
    verify(categoryRepo).save(any(Category.class));
  }

  @Test
  @DisplayName("updateCategory throws if not admin")
  void updateCategoryUnauthorized() {
    CategoryCreateRequest request = new CategoryCreateRequest("Updated", "Oppdatert", 150, false);
    assertThrows(IllegalArgumentException.class, () ->
        categoryService.updateCategory(1, request, "Bearer bad-token")
    );
  }

  @Test
  @DisplayName("updateCategory throws if category not found")
  void updateCategoryNotFound() {
    CategoryCreateRequest request = new CategoryCreateRequest("Updated", "Oppdatert", 150, false);
    when(categoryRepo.findById(1)).thenReturn(java.util.Optional.empty());

    assertThrows(NoSuchElementException.class, () ->
        categoryService.updateCategory(1, request, "Bearer valid-token")
    );
  }

  @Test
  @DisplayName("deleteCategory succeeds with valid input")
  void deleteCategorySuccess() {
    when(itemRepo.findByCategoryId(Mockito.anyInt())).thenReturn(Collections.emptyList());
    when(categoryRepo.findById(1)).thenReturn(Optional.of(testCategory));
    when(categoryRepo.findByEnglishName("Other")).thenReturn(Optional.of(new Category("Other", "Annet", 100, true)));
    assertDoesNotThrow(() -> categoryService.deleteCategory(1, "Bearer valid-token"));
    verify(categoryRepo).delete(testCategory);
  }

  @Test
  @DisplayName("deleteCategory throws if not admin")
  void deleteCategoryUnauthorized() {
    assertThrows(IllegalArgumentException.class, () ->
        categoryService.deleteCategory(1, "Bearer invalid-token")
    );
  }

  @Test
  @DisplayName("deleteCategory throws if category not found")
  void deleteCategoryNotFound() {
    when(categoryRepo.findById(1)).thenReturn(java.util.Optional.empty());

    assertThrows(NoSuchElementException.class, () ->
        categoryService.deleteCategory(1, "Bearer valid-token")
    );
  }




}
