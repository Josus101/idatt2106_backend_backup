package org.ntnu.idatt2106.backend.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserTest {

  @Test
  @DisplayName("Test User constructor sets fields correctly")
  void testConstructorSetsFields() {
    User user = new User(
        "test@example.com",
        "securePass",
        "John",
        "Doe",
        "12345678"
    );

    assertEquals("test@example.com", user.getEmail());
    assertEquals("securePass", user.getPassword());
    assertEquals("John", user.getFirstname());
    assertEquals("Doe", user.getLastname());
    assertEquals("12345678", user.getPhoneNumber());
  }

  @Test
  @DisplayName("Test User constructor with ID and user details sets fields correctly")
  void testConstructorWithIdAndDetails() {
    User user = new User(101, "test@example.com", "securePass", "John", "Doe", "12345678");

    assertEquals(101, user.getId());
    assertEquals("test@example.com", user.getEmail());
    assertEquals("securePass", user.getPassword());
    assertEquals("John", user.getFirstname());
    assertEquals("Doe", user.getLastname());
    assertEquals("12345678", user.getPhoneNumber());

    // Assuming this constructor sets latitude and longitude to 0.0
    assertEquals(0.0f, user.getLatitude());
    assertEquals(0.0f, user.getLongitude());
  }

  @Test
  @DisplayName("Test setId and getId")
  void testIdField() {
    User user = new User();
    user.setId(42);
    assertEquals(42, user.getId());
  }

  @Test
  @DisplayName("Test setEmail and getEmail")
  void testEmailField() {
    User user = new User();
    user.setEmail("user@example.com");
    assertEquals("user@example.com", user.getEmail());
  }

  @Test
  @DisplayName("Test setPassword and getPassword")
  void testPasswordField() {
    User user = new User();
    user.setPassword("myPassword");
    assertEquals("myPassword", user.getPassword());
  }

  @Test
  @DisplayName("Test setFirstname and getFirstname")
  void testFirstnameField() {
    User user = new User();
    user.setFirstname("Alice");
    assertEquals("Alice", user.getFirstname());
  }

  @Test
  @DisplayName("Test setLastname and getLastname")
  void testSurnameField() {
    User user = new User();
    user.setLastname("Smith");
    assertEquals("Smith", user.getLastname());
  }

  @Test
  @DisplayName("Test setPhoneNumber and getPhoneNumber")
  void testPhoneNumberField() {
    User user = new User();
    user.setPhoneNumber("12345678");
    assertEquals("12345678", user.getPhoneNumber());
  }

  @Test
  @DisplayName("Test setLatitude and getLatitude")
  void testLatitudeField() {
    User user = new User();
    user.setLatitude(45.5f);
    assertEquals(45.5f, user.getLatitude());
  }

  @Test
  @DisplayName("Test setLongitude and getLongitude")
  void testLongitudeField() {
    User user = new User();
    user.setLongitude(12.34f);
    assertEquals(12.34f, user.getLongitude());
  }

  @Test
  @DisplayName("Test getStringID returns correct string")
  void testGetStringID() {
    User user = new User();
    user.setId(101);
    assertEquals("101", user.getStringID());
  }

  @Test
  @DisplayName("Test toString returns full name")
  void testToString() {
    User user = new User(
        "email@domain.com",
        "pass",
        "Jane",
        "Doe",
        "99887766"
    );
    assertEquals("Jane Doe", user.toString());
  }

  //@Test
  //@DisplayName("Test adding household to user adds correctly")
  //void testAddHousehold() {
  //  User user = new User();
  //  Household household = new Household();
  //  HouseholdMembers householdMembership = new HouseholdMembers();
  //  user.addHouseholdMembership(household);
  //  assertTrue(user.getHouseholds().contains(household));
  //}
}
