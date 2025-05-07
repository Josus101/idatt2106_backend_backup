package org.ntnu.idatt2106.backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

  Admin testAdmin;

  @BeforeEach
  void beforeEach() {
    testAdmin = new Admin();
  }
  @Test
  @DisplayName("Test Admin constructor sets fields correctly")
  void testConstructorSetsFields() {
    Admin admin = new Admin(
        "adminUsername",
        "securePassword",
        "test@mail.com",
        false
    );

    assertEquals(0, admin.getId());
    assertEquals("adminUsername", admin.getUsername());
    assertEquals("securePassword", admin.getPassword());
    assertFalse(admin.isSuperUser());

    // test empty constructor
    Admin admin1 = new Admin();
    assertNotNull(admin1);
  }

  @Test
  @DisplayName("Test setId and getId")
  void testIdField() {
    testAdmin.setId(1);
    assertEquals(1, testAdmin.getId());
  }

  @Test
  @DisplayName("Test setUsername and getUsername")
  void testUsernameField() {
    testAdmin.setUsername("adminUser");
    assertEquals("adminUser", testAdmin.getUsername());
  }

  @Test
  @DisplayName("Test setPassword and getPassword")
  void testPasswordField() {
    testAdmin.setPassword("adminPass");
    assertEquals("adminPass", testAdmin.getPassword());
  }

  @Test
  @DisplayName("Test setSuper and isSuper")
  void testSuperField() {
    testAdmin.setSuperUser(true);
    assertTrue(testAdmin.isSuperUser());

    testAdmin.setSuperUser(false);
    assertFalse(testAdmin.isSuperUser());
  }

  @Test
  @DisplayName("Test toString method")
  void testToString() {
    testAdmin.setUsername("adminUser");

    assertEquals("adminUser", testAdmin.toString());
  }
}