package org.ntnu.idatt2106.backend.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {

  @Test
  @DisplayName("Test Item constructor sets fields correctly")
  void testConstructorSetsFields() {
    Date date = new Date();
    Item item = new Item(1, "Milk", 2.5, "liters", date);

    assertEquals(1, item.getId());
    assertEquals("Milk", item.getName());
    assertEquals(2.5, item.getAmount());
    assertEquals("liters", item.getUnit());
    assertEquals(date, item.getExpirationDate());
  }

  @Test
  @DisplayName("Test setId and getId")
  void testIdField() {
    Item item = new Item();
    item.setId(42);
    assertEquals(42, item.getId());
  }

  @Test
  @DisplayName("Test setName and getName")
  void testNameField() {
    Item item = new Item();
    item.setName("Cheese");
    assertEquals("Cheese", item.getName());
  }

  @Test
  @DisplayName("Test setAmount and getAmount")
  void testAmountField() {
    Item item = new Item();
    item.setAmount(3.75);
    assertEquals(3.75, item.getAmount());
  }

  @Test
  @DisplayName("Test setUnit and getUnit")
  void testUnitField() {
    Item item = new Item();
    item.setUnit("kg");
    assertEquals("kg", item.getUnit());
  }

  @Test
  @DisplayName("Test setExpirationDate and getExpirationDate")
  void testExpirationDateField() {
    Item item = new Item();
    Date now = new Date();
    item.setExpirationDate(now);
    assertEquals(now, item.getExpirationDate());
  }

  @Test
  @DisplayName("Test setCategory and getCategory")
  void testCategoryField() {
    Item item = new Item();
    Category category = new Category();
    category.setId(1);
    category.setName("Dairy");
    item.setCategory(category);
    assertEquals(category, item.getCategory());
  }

  @Test
  @DisplayName("Test setHousehold and getHousehold")
  void testHouseholdField() {
    Item item = new Item();
    Household household1 = new Household();
    Household household2 = new Household();
    item.setHousehold(java.util.List.of(household1, household2));
    assertEquals(2, item.getHousehold().size());
  }
}
