package org.ntnu.idatt2106.backend.dto.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PasswordResetRequestTest {

  @Test
  @DisplayName("Test PasswordResetRequest constructor and getter/setter methods")
  void testConstructorAndGettersSetters() {
    PasswordResetRequest request = new PasswordResetRequest();
    String testPassword = "newPassword123";
    request.setPassword(testPassword);
    assertEquals(testPassword, request.getPassword());
  }

  @Test
  @DisplayName("Test PasswordResetRequest default constructor")
  void testDefaultConstructor() {
    PasswordResetRequest request = new PasswordResetRequest();
    assertNull(request.getPassword());
  }
  @Test
  @DisplayName("Test PasswordResetRequest constructor with parameters")
  void testConstructorWithParameters() {
    String testPassword = "newPassword123";
    PasswordResetRequest request = new PasswordResetRequest(testPassword);
    assertEquals(testPassword, request.getPassword());
  }

}