package org.ntnu.idatt2106.backend.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HouseholdTest {

  Household testHousehold;
  @BeforeEach
  void setUp() {
    testHousehold = new Household();
  }


  @Test
  @DisplayName("Test Household constructor sets fields correctly")
  void testConstructorSetsFields() {
    Household household = new Household(
        1,
        "Test Household",
        10.0,
        20.0
    );

    assertEquals(1, household.getId());
    assertEquals("Test Household", household.getName());
    assertEquals(10.0, household.getLatitude());
    assertEquals(20.0, household.getLongitude());
  }

  @Test
  void testEmptyConstructor() {
    Household household = new Household();
    assertNotNull(household);
  }

  @Test
  void testFullConstructor() {
    List<HouseholdMembers> members = new ArrayList<>();
    List<Item> inventory = new ArrayList<>();

    members.add(new HouseholdMembers());
    inventory.add(new Item());

    Household household = new Household(1, "Test Household", 10.0, 20.0, members, inventory);
    assertEquals(1, household.getId());
    assertEquals("Test Household", household.getName());
    assertEquals(10.0, household.getLatitude());
    assertEquals(20.0, household.getLongitude());

    assertEquals(1, household.getMembers().size());
    assertEquals(1, household.getInventory().size());
    assertEquals(members.get(0), household.getMembers().get(0));
    assertEquals(inventory.get(0), household.getInventory().get(0));
  }


  @Test
  @DisplayName("Test setId and getId")
  void testIdField() {
    testHousehold.setId(1);
    assertEquals(1, testHousehold.getId());
  }

  @Test
  @DisplayName("Test setName and getName")
  void testNameField() {
    testHousehold.setName("Test Household");
    assertEquals("Test Household", testHousehold.getName());
  }

  @Test
  @DisplayName("Test setLatitude and getLatitude")
  void testLatitudeField() {
    testHousehold.setLatitude(10.0);
    assertEquals(10.0, testHousehold.getLatitude());
  }

  @Test
  @DisplayName("Test setLongitude and getLongitude")
  void testLongitudeField() {
    testHousehold.setLongitude(20.0);
    assertEquals(20.0, testHousehold.getLongitude());
  }

  @Test
  @DisplayName("Test setMembers and getMembers")
  void testMembersField() {
    HouseholdMembers member = new HouseholdMembers();
    testHousehold.setMembers(List.of(member));
    assertEquals(1, testHousehold.getMembers().size());
    assertEquals(member, testHousehold.getMembers().get(0));
  }

  @Test
  @DisplayName("Test setInventory and getInventory")
  void testInventoryField() {
    Item item = new Item();
    testHousehold.setInventory(List.of(item));
    assertEquals(1, testHousehold.getInventory().size());
    assertEquals(item, testHousehold.getInventory().get(0));
  }

  @Test
  @DisplayName("Test constructor throws IllegalArgumentException when member is null")
  void testConstructorThrowsIllegalArgumentExceptionWhenMemberIsNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      new HouseholdMembers(null, testHousehold, false, false);
    });
  }

  @Test
  @DisplayName("Test constructor throws IllegalArgumentException when household is null")
  void testConstructorThrowsIllegalArgumentExceptionWhenHouseholdIsNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      new HouseholdMembers(new User(), null, false, false);
    });
  }

}