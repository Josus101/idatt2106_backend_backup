package org.ntnu.idatt2106.backend.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CategoryTest {

  @Test
  @DisplayName("Test Category constructor sets fields correctly")
  void testConstructorSetsFields() {
    Category category = new Category("Test Category");

    assertEquals("Test Category", category.getName());
  }
  @Test
  @DisplayName("Test Get Id method gets id correctly")
  void getIdReturnsCorrectly() {
    Category category = new Category();
    category.setId(1);
    assertEquals(1, category.getId());
  }

  @Test
  @DisplayName("Test Get Name method gets name correctly")
  void getNameReturnsCorrectly() {
    Category category = new Category();
    category.setName("Test Category");
    assertEquals("Test Category", category.getName());
  }

  @Test
  @DisplayName("Test Set Name method sets name correctly")
  void setNameSetsCorrectly() {
    Category category = new Category();
    category.setName("Test Category");
    assertEquals("Test Category", category.getName());
  }

}