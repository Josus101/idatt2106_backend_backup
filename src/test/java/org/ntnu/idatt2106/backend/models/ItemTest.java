package org.ntnu.idatt2106.backend.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ntnu.idatt2106.backend.models.Category;
import org.ntnu.idatt2106.backend.models.Household;
import org.ntnu.idatt2106.backend.models.Item;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {

  @Test
  @DisplayName("Test all-args constructor sets fields correctly")
  void testConstructorSetsFields() {
    Date date = new Date();
    Item item = new Item(1, "Milk", 2.0, "liters", date);

    assertEquals(1, item.getId());
    assertEquals("Milk", item.getName());
    assertEquals(2.0, item.getAmount());
    assertEquals("liters", item.getUnit());
    assertEquals(date, item.getExpirationDate());
  }

  @Test
  @DisplayName("Test setId and getId")
  void testIdField() {
    Item item = new Item();
    item.setId(5);
    assertEquals(5, item.getId());
  }

  @Test
  @DisplayName("Test setName and getName")
  void testNameField() {
    Item item = new Item();
    item.setName("Bread");
    assertEquals("Bread", item.getName());
  }

  @Test
  @DisplayName("Test setAmount and getAmount")
  void testAmountField() {
    Item item = new Item();
    item.setAmount(1.5);
    assertEquals(1.5, item.getAmount());
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
    Date date = new Date();
    item.setExpirationDate(date);
    assertEquals(date, item.getExpirationDate());
  }

  @Test
  @DisplayName("Test setHousehold and getHousehold")
  void testHouseholdField() {
    Item item = new Item();
    Household h1 = new Household();
    Household h2 = new Household();
    List<Household> households = Arrays.asList(h1, h2);
    item.setHousehold(households);
    assertEquals(households, item.getHousehold());
  }

  @Test
  @DisplayName("Test setCategory and getCategory")
  void testCategoryField() {
    Item item = new Item();
    Category category = new Category();
    item.setCategory(category);
    assertEquals(category, item.getCategory());
  }
}
