package org.ntnu.idatt2106.backend.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ntnu.idatt2106.backend.dto.user.UserPositionUpdate;

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

  @Test
  @DisplayName("Test adding household to user adds correctly")
  void testAddHousehold() {
    User user = new User();
    Household household = new Household();
    HouseholdMembers householdMembership = new HouseholdMembers(user, household, false);
    user.getHouseholdMemberships().add(householdMembership);
    assertTrue(user.getHouseholdMemberships().contains(householdMembership));
  }

  @Test
  @DisplayName("Test setPosition sets latitude and longitude correctly")
  void testSetPosition() {
    User user = new User();
    UserPositionUpdate positionUpdate = new UserPositionUpdate(69.4, 18.9);
    user.setPosition(positionUpdate);
    assertEquals(69.4, user.getLatitude());
    assertEquals(18.9, user.getLongitude());
  }

  @Test
  @DisplayName("Test setPosition throws exception for invalid latitude")
  void testSetPositionInvalidLatitude() {
    User user = new User();
    UserPositionUpdate positionUpdate = new UserPositionUpdate(91.0, 18.9);
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      user.setPosition(positionUpdate);
    });
    assertEquals("Invalid latitude or longitude", exception.getMessage());
  }

  @Test
  @DisplayName("Test setPosition throws exception for invalid longitude")
  void testSetPositionInvalidLongitude() {
    User user = new User();
    UserPositionUpdate positionUpdate = new UserPositionUpdate(69.4, 181.0);
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      user.setPosition(positionUpdate);
    });
    assertEquals("Invalid latitude or longitude", exception.getMessage());
  }

  @Test
  @DisplayName("Test setPosition sets time correctly")
  void testSetPositionTimeFormat() {
    User user = new User();
    UserPositionUpdate positionUpdate = new UserPositionUpdate(69.4, 18.9);
    user.setPosition(positionUpdate);
    assertNotNull(user.getPositionUpdateTime());
    assertTrue(user.getPositionUpdateTime() instanceof Date);
  }

  @Test
  @DisplayName("formats date correctly")
  void testFormatDate() {
    User user = new User();
    Date date = new Date(1746442184323L);
    user.setPositionUpdateTime(date);

    assertEquals("2025-05-05T12:49", user.getFormattedPositionUpdateTime());
  }


}
