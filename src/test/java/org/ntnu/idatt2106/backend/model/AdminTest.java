package org.ntnu.idatt2106.backend.model;

import org.junit.jupiter.api.BeforeAll;
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
            123,
            "adminUsername",
            "securePassword",
            false
    );

    assertEquals(123, admin.getId());
    assertEquals("adminUsername", admin.getUsername());
    assertEquals("securePassword", admin.getPassword());
    assertFalse(admin.isSuper());

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
    testAdmin.setSuper(true);
    assertTrue(testAdmin.isSuper());

    testAdmin.setSuper(false);
    assertFalse(testAdmin.isSuper());
  }
}