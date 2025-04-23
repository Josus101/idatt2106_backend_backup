package org.ntnu.idatt2106.backend.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ntnu.idatt2106.backend.dto.UserTokenDTO;
import org.ntnu.idatt2106.backend.model.User;

public class JWTTokenTest {
  JWT_token jwt;
  User user;
  @BeforeEach
  void setUp() {
    jwt = new JWT_token();
    user = new User(
        "ape@ape.ape",
        "password123",
        "Kalle",
        "Kontainer",
        "12345678"
    );
  }

  @Test
  @DisplayName("Test generate token method generates a token")
  void testGenerateToken() {
    UserTokenDTO token = jwt.generateJwtToken(user);
    assertNotNull(token.getToken());
  }

  @Test
  @DisplayName("Test generate token method generates a token with correct user id")
  void testGenerateTokenUsesCorrectId() {
    UserTokenDTO token = jwt.generateJwtToken(user);
    String userId = jwt.extractIdFromJwt(token.getToken());
    assertNotNull(userId);
    assertEquals(user.getStringID(), userId);
  }
  @Test
  @DisplayName("Token validation throws IllegalArgumentException for malformed token")
  void testValidateMalformedToken() {
    String malformedToken = "this.is.not.a.jwt";
    assertThrows(IllegalArgumentException.class, () -> {
      jwt.validateJwtToken(malformedToken);
    });
  }
  @Test
  @DisplayName("Token validation throws IllegalArgumentException for empty token")
  void testValidateEmptyToken() {
    assertThrows(IllegalArgumentException.class, () -> {
      jwt.validateJwtToken("");
    });
  }
  @Test
  @DisplayName("extractIdFromJwt returns null for invalid token")
  void testExtractIdFromInvalidToken() {
    String invalidToken = "invalid.token.value";
    String id = jwt.extractIdFromJwt(invalidToken);
    assertNull(id);
  }







}
