package org.ntnu.idatt2106.backend.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HasherTest {
  BCryptHasher hasher;
  @BeforeEach
  void setUp() {
    hasher = new BCryptHasher();
  }
  @Test
  @DisplayName("Hashing hashes the password correctly")
  void testHashingShouldHash() {
    String password = "password123";
    String hashedPassword = hasher.hashPassword(password);
    assertNotEquals(password, hashedPassword);
  }
  @Test
  @DisplayName("Hashing should not be the same for different passwords")
  void testHashingShouldNotBeSame() {
    String password1 = "password123";
    String password2 = "password456";
    String password3 = "Password123";
    String hashedPassword1 = hasher.hashPassword(password1);
    String hashedPassword2 = hasher.hashPassword(password2);
    String hashedPassword3 = hasher.hashPassword(password3);
    assertNotEquals(hashedPassword1, hashedPassword2);
    assertNotEquals(hashedPassword1, hashedPassword3);
    assertNotEquals(hashedPassword2, hashedPassword3);
  }
  @Test
  @DisplayName("checkPassword should return true for correct password")
  void testHashingShouldVerify() {
    String password = "password123";
    String hashedPassword = hasher.hashPassword(password);
    assertTrue(hasher.checkPassword(password, hashedPassword));
  }
  @Test
  @DisplayName("checkPassword should return false for incorrect password")
  void testHashingShouldNotVerify() {
    String password = "password123";
    String hashedPassword = hasher.hashPassword(password);
    assertFalse(hasher.checkPassword("wrongpassword", hashedPassword));
    assertFalse(hasher.checkPassword("Password123", hashedPassword));
  }

  @Test
  @DisplayName("checkPassword should return false for empty password")
  void testHashingShouldNotVerifyEmptyPassword() {
    String password = "password123";
    String hashedPassword = hasher.hashPassword(password);
    assertFalse(hasher.checkPassword("", hashedPassword));
    assertFalse(hasher.checkPassword(null, hashedPassword));
  }

  @Test
  @DisplayName("checkPassword should return false for empty hash")
  void testHashingShouldNotVerifyEmptyHash() {
    String password = "password123";
    assertFalse(hasher.checkPassword(password, ""));
    assertFalse(hasher.checkPassword(password, null));
  }

  @Test
  @DisplayName("hashPassword should return null for empty password")
  void testHashingShouldReturnNullForEmptyPassword() {
    String hashedPassword = hasher.hashPassword("");
    assertNull(hashedPassword);
    hashedPassword = hasher.hashPassword(null);
    assertNull(hashedPassword);
  }

}
