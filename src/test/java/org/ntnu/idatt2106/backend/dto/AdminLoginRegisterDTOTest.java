package org.ntnu.idatt2106.backend.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ntnu.idatt2106.backend.dto.admin.AdminLoginRegisterDTO;

public class AdminLoginRegisterDTOTest {

  @Test
  @DisplayName("Test all-args constructor sets fields correctly")
  void testConstructorSetsFields() {
    AdminLoginRegisterDTO dto = new AdminLoginRegisterDTO("adminUser", "securePass");

    assertEquals("adminUser", dto.getUsername());
    assertEquals("securePass", dto.getPassword());
  }

  @Test
  @DisplayName("Test setUsername and getUsername")
  void testUsernameField() {
    AdminLoginRegisterDTO dto = new AdminLoginRegisterDTO("initUser", "pass");
    dto.setUsername("newUser");
    assertEquals("newUser", dto.getUsername());
  }

  @Test
  @DisplayName("Test setPassword and getPassword")
  void testPasswordField() {
    AdminLoginRegisterDTO dto = new AdminLoginRegisterDTO("admin", "initialPass");
    dto.setPassword("newPass");
    assertEquals("newPass", dto.getPassword());
  }
}
