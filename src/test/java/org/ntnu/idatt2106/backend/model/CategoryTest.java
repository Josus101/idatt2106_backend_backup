package org.ntnu.idatt2106.backend.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CategoryTest {
  private Category testCategory;
  @BeforeEach
  void setUp() {
    testCategory = new Category(1, "Test Category", "Test kategori", null, false);
  }

  @Test
  @DisplayName("Test constructor with id and name")
  void testConstructorSetsFields() {
    assertEquals(1, testCategory.getId());
    assertEquals("Test Category", testCategory.getEnglishName());
    assertEquals("Test kategori", testCategory.getNorwegianName());
  }

  @Test
  @DisplayName("Test constructor with id and name")
  void testDefaultConstructor() {
    Category category = new Category();
    assertNotNull(category);
    assertEquals(0, category.getId());
    assertNull(category.getEnglishName());
    assertNull(category.getNorwegianName());
  }

  @Test
  @DisplayName("Test name field")
  void testIdField() {
    testCategory.setId(2);
    assertEquals(2, testCategory.getId());
  }

  @Test
  @DisplayName("Test no id constructor field")
   void testNoIdConstructor() {
    Category category = new Category("Test Category", "Test kategori", 2, false);
    assertEquals("Test Category", category.getEnglishName());
    assertEquals("Test kategori", category.getNorwegianName());
    assertEquals(2, category.getKcalPerUnit());
    assertFalse(category.getIsEssential());
    }
}