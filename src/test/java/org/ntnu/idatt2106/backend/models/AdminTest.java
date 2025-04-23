package org.ntnu.idatt2106.backend.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ntnu.idatt2106.backend.models.Admin;

public class AdminTest {

  @Test
  @DisplayName("Test Admin constructor sets fields correctly")
  void testConstructorSetsFields() {
    Admin admin = new Admin("adminUser", "adminPass", true);

    assertEquals("adminUser", admin.getUsername());
    assertEquals("adminPass", admin.getPassword());
    assertTrue(admin.isSuperUser());
  }

  @Test
  @DisplayName("Test setId and getId")
  void testIdField() {
    Admin admin = new Admin("admin", "pass", false);
    admin.setId(99);
    assertEquals(99, admin.getId());
  }

  @Test
  @DisplayName("Test setUsername and getUsername")
  void testUsernameField() {
    Admin admin = new Admin("initial", "pass", false);
    admin.setUsername("newAdmin");
    assertEquals("newAdmin", admin.getUsername());
  }

  @Test
  @DisplayName("Test setPassword and getPassword")
  void testPasswordField() {
    Admin admin = new Admin("admin", "oldPass", false);
    admin.setPassword("newPass");
    assertEquals("newPass", admin.getPassword());
  }

  @Test
  @DisplayName("Test setIsSuperUser and getIsSuperUser")
  void testIsSuperUserField() {
    Admin admin = new Admin("admin", "pass", false);
    admin.setSuperUser(true);
    assertTrue(admin.isSuperUser());
  }

  @Test
  @DisplayName("Test getStringID returns correct string")
  void testGetStringID() {
    Admin admin = new Admin("admin", "pass", false);
    admin.setId(5);
    assertEquals("5", admin.getStringID());
  }

  @Test
  @DisplayName("Test toString returns username")
  void testToString() {
    Admin admin = new Admin("superAdmin", "pass", true);
    assertEquals("superAdmin", admin.toString());
  }
}
