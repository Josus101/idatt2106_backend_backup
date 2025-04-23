package org.ntnu.idatt2106.backend.models;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HouseHoldTest {

  @Test
  @DisplayName("Should return true when HouseholdMembersId is the same object")
  void testEqualsSameObject() {
    HouseholdMembersId id = new HouseholdMembersId(1, 2);
    assertEquals(id, id);
  }

  @Test
  @DisplayName("Should return true when HouseholdMembersId objects are equal")
  void testEqualsEqualObjects() {
    HouseholdMembersId id1 = new HouseholdMembersId(1, 2);
    HouseholdMembersId id2 = new HouseholdMembersId(1, 2);
    assertEquals(id1, id2);
  }

  @Test
  @DisplayName("Should return false when HouseholdMembersId has different user")
  void testNotEqualsDifferentUser() {
    HouseholdMembersId id1 = new HouseholdMembersId(1, 2);
    HouseholdMembersId id2 = new HouseholdMembersId(3, 2);
    assertNotEquals(id1, id2);
  }

  @Test
  @DisplayName("Should return false when HouseholdMembersId has different household")
  void testNotEqualsDifferentHousehold() {
    HouseholdMembersId id1 = new HouseholdMembersId(1, 2);
    HouseholdMembersId id2 = new HouseholdMembersId(1, 3);
    assertNotEquals(id1, id2);
  }

  @Test
  @DisplayName("Should have consistent hash code for equal HouseholdMembersId objects")
  void testHashCodeConsistency() {
    HouseholdMembersId id1 = new HouseholdMembersId(1, 2);
    HouseholdMembersId id2 = new HouseholdMembersId(1, 2);
    assertEquals(id1.hashCode(), id2.hashCode());
  }

  @Test
  @DisplayName("Should correctly initialize HouseholdMembers using constructor")
  void testConstructorInitializesFieldsCorrectly() {
    User user = new User();
    user.setId(1);
    Household household = new Household();
    household.setId(2);

    HouseholdMembers members = new HouseholdMembers(user, household, true);

    assertNotNull(members);
    assertNotNull(members.getId());
    assertEquals(1, members.getId().getUser());
    assertEquals(user, members.getUser());
    assertEquals(household, members.getHousehold());
    assertTrue(members.isAdmin());
  }

  @Test
  @DisplayName("Should correctly use setter and getter methods for HouseholdMembers")
  void testSettersAndGettersForHouseHoldMembers() {
    HouseholdMembers members = new HouseholdMembers();
    members.setAdmin(false);
    assertFalse(members.isAdmin());
  }

  @Test
  @DisplayName("Should assign all fields correctly when using Household constructor")
  void testConstructorAssignsAllFields() {
    HouseholdMembers member = new HouseholdMembers();
    Item item = new Item();

    Household household = new Household(1, "Test Home", 60.0, 5.0,
        List.of(member), List.of(item));

    assertEquals(1, household.getId());
    assertEquals("Test Home", household.getName());
    assertEquals(60.0, household.getLatitude());
    assertEquals(5.0, household.getLongitude());
    assertEquals(1, household.getMembers().size());
    assertEquals(1, household.getInventory().size());
  }

  @Test
  @DisplayName("Should correctly use setter and getter methods for Household")
  void testSettersAndGettersForHouseHold() {
    Household household = new Household();
    household.setName("New Home");
    household.setLatitude(65.0);
    household.setLongitude(10.0);

    assertEquals("New Home", household.getName());
    assertEquals(65.0, household.getLatitude());
    assertEquals(10.0, household.getLongitude());
  }
}
