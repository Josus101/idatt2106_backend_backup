package org.ntnu.idatt2106.backend.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ntnu.idatt2106.backend.dto.admin.AdminLoginRegisterRequest;

public class AdminLoginRegisterDTOTest {

  @Test
  @DisplayName("Test all-args constructor sets fields correctly")
  void testConstructorSetsFields() {
    AdminLoginRegisterRequest dto = new AdminLoginRegisterRequest("adminUser","test@mail.com", "securePass");

    assertEquals("adminUser", dto.getUsername());
    assertEquals("securePass", dto.getPassword());
  }

  @Test
  @DisplayName("Test setUsername and getUsername")
  void testUsernameField() {
    AdminLoginRegisterRequest dto = new AdminLoginRegisterRequest("initUser","test@mail.com", "pass");
    dto.setUsername("newUser");
    assertEquals("newUser", dto.getUsername());
  }

  @Test
  @DisplayName("Test setPassword and getPassword")
  void testPasswordField() {
    AdminLoginRegisterRequest dto = new AdminLoginRegisterRequest("admin","test@mail.com", "initialPass");
    dto.setPassword("newPass");
    assertEquals("newPass", dto.getPassword());
  }
}
