package org.ntnu.idatt2106.backend.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HouseholdMembersTest {

  HouseholdMembers testAdminHouseholdMembers;
  HouseholdMembers testHouseholdMembers;


  @BeforeEach
  void setUp() {
    testAdminHouseholdMembers = new HouseholdMembers();
    testHouseholdMembers = new HouseholdMembers();
  }

  @Test
  @DisplayName("Test HouseholdMembers constructor sets fields correctly")
  void testConstructorSetsFields() {
    User user = new User();
    Household household = new Household();
    boolean isAdmin = true;

    HouseholdMembers householdMembers = new HouseholdMembers(user, household, isAdmin);

    assertEquals(user, householdMembers.getUser());
    assertEquals(household, householdMembers.getHousehold());
    assertTrue(householdMembers.isAdmin());
  }

  @Test
  @DisplayName("Test empty constructor")
  void testEmptyConstructor() {
    HouseholdMembers householdMembers = new HouseholdMembers();
    assertNotNull(householdMembers);
  }

  @Test
  @DisplayName("Test setUser and getUser")
  void testUserField() {
    User user = new User();
    testAdminHouseholdMembers.setUser(user);
    assertEquals(user, testAdminHouseholdMembers.getUser());
  }

  @Test
  @DisplayName("Test setHousehold and getHousehold")
  void testHouseholdField() {
    Household household = new Household();
    testAdminHouseholdMembers.setHousehold(household);
    assertEquals(household, testAdminHouseholdMembers.getHousehold());
  }

  @Test
  @DisplayName("Test setAdmin and isAdmin")
  void testAdminField() {
    testAdminHouseholdMembers.setAdmin(true);
    assertTrue(testAdminHouseholdMembers.isAdmin());
  }

  @Test
  @DisplayName("Test setId and getId")
  void testIdField() {
    HouseholdMembersId id = new HouseholdMembersId(1, 2);
    testAdminHouseholdMembers.setId(id);
    assertEquals(id, testAdminHouseholdMembers.getId());
  }

}