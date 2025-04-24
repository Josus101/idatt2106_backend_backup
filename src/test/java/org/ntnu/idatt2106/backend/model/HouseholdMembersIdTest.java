package org.ntnu.idatt2106.backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HouseholdMembersIdTest {

  HouseholdMembersId householdMembersId1;


  @BeforeEach
  void setUp() {
    householdMembersId1 = new HouseholdMembersId(1, 1);
  }

  @Test
  void testConstructorSetsFields() {
    HouseholdMembersId householdMembersId = new HouseholdMembersId(1, 2);
    assertEquals(1, householdMembersId.getUser());
    assertEquals(2, householdMembersId.getHousehold());
  }

  @Test
  @DisplayName("Test default constructor sets fields to zero")
  void testDefaultConstructor() {
    HouseholdMembersId householdMembersId = new HouseholdMembersId();
    assertNotNull(householdMembersId);
  }

  @Test
  @DisplayName("Test setUser and getUser")
  void testUserField() {
    householdMembersId1.setUser(3);
    assertEquals(3, householdMembersId1.getUser());
  }

  @Test
  @DisplayName("Test setHousehold and getHousehold")
  void testHouseholdField() {
    householdMembersId1.setHousehold(2);
    assertEquals(2, householdMembersId1.getHousehold());
  }

  @Test
  @DisplayName("Test Equals with same Object")
  void testEqualsWithSameObject() {
    assertTrue(householdMembersId1.equals(householdMembersId1));
  }

  @Test
  @DisplayName("Test Equals with different Object")
  void testEqualsWithDifferentObject() {
    HouseholdMembersId householdMembersId2 = new HouseholdMembersId(1, 2);
    assertFalse(householdMembersId1.equals(householdMembersId2));
  }

  @Test
  @DisplayName("Test Equals with null Object")
  void testEqualsWithNull() {
    assertFalse(householdMembersId1.equals(null));
  }

  @Test
  @DisplayName("Test Equals with different class")
  void testEqualsWithDifferentClass() {
    assertFalse(householdMembersId1.equals("Not a HouseholdMembersId"));
  }

  @Test
  @DisplayName("Test Equals with same values")
  void testEqualsWithSameValues() {
    HouseholdMembersId householdMembersId2 = new HouseholdMembersId(1, 1);
    assertTrue(householdMembersId1.equals(householdMembersId2));
  }

  @Test
  @DisplayName("Test equals with different user but same household")
  void testEqualsDifferentUserSameHousehold() {
    HouseholdMembersId other = new HouseholdMembersId(99, 1); // different user, same household
    assertFalse(householdMembersId1.equals(other));
  }


  @Test
  @DisplayName("Test equals with same user but different household")
  void testEqualsSameUserDifferentHousehold() {
    HouseholdMembersId other = new HouseholdMembersId(1, 99); // same user, different household
    assertFalse(householdMembersId1.equals(other));
  }


  @Test
  void testHashCode() {
    HouseholdMembersId householdMembersId2 = new HouseholdMembersId(1, 1);
    assertEquals(householdMembersId1.hashCode(), householdMembersId2.hashCode());
  }

  @Test
  void testHashCodeWithDifferentValues() {
    HouseholdMembersId householdMembersId2 = new HouseholdMembersId(1, 2);
    assertNotEquals(householdMembersId1.hashCode(), householdMembersId2.hashCode());
  }

}