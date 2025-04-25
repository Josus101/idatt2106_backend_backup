package org.ntnu.idatt2106.backend.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.ntnu.idatt2106.backend.dto.user.UserTokenResponse;
import org.ntnu.idatt2106.backend.model.Admin;
import org.ntnu.idatt2106.backend.model.User;

import org.ntnu.idatt2106.backend.exceptions.TokenExpiredException;
import org.ntnu.idatt2106.backend.repo.AdminRepo;
import org.ntnu.idatt2106.backend.repo.UserRepo;

import java.util.Optional;

public class JWTTokenTest {
  @Mock
  JWT_token jwt;
  User user;
  Admin admin;

  @Mock
  private UserRepo userRepo;

  @Mock
  private AdminRepo adminRepo;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    jwt = new JWT_token(userRepo, adminRepo);
    user = new User(
        "ape@ape.ape",
        "password123",
        "Kalle",
        "Kontainer",
        "12345678"
    );
    admin = new Admin(
        "theBigBossMan",
        "password123",
        false
    );
  }

  @Test
  @DisplayName("Test default constructor creates JWT_token instance")
  void testDefaultConstructor() {
    JWT_token jwt = new JWT_token();
    assertNotNull(jwt);
  }

  @Test
  @DisplayName("Test constructor with UserRepo creates JWT_token instance")
  void testConstructorWithUserRepo() {
    JWT_token jwt = new JWT_token(userRepo, adminRepo);
    assertNotNull(jwt);
  }

  @Test
  @DisplayName("Test generate token method generates a token")
  void testGenerateToken() {
    UserTokenResponse token = jwt.generateJwtToken(user);
    assertNotNull(token.getToken());
  }

  @Test
  @DisplayName("Test generate token method generates a token with correct user id")
  void testGenerateTokenUsesCorrectId() {
    UserTokenResponse token = jwt.generateJwtToken(user);
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
  @DisplayName("Token validation throws TokenExpiredException for expired token")
  void testValidateExpiredToken() {
    String expiredToken = jwt.generateJwtTokenWithExpirationTime(user, -1000).getToken();

    assertThrows(TokenExpiredException.class, () -> {
      jwt.validateJwtToken(expiredToken);
    });
  }

  @Test
  @DisplayName("Token validation does not throw exception for valid token")
  void testValidateValidToken() {
    String validToken = jwt.generateJwtToken(user).getToken();
    assertDoesNotThrow(() -> {
      jwt.validateJwtToken(validToken);
    });
  }

  @Test
  @DisplayName("extractIdFromJwt returns null for invalid token")
  void testExtractIdFromInvalidToken() {
    String invalidToken = "invalid.token.value";
    String id = jwt.extractIdFromJwt(invalidToken);
    assertNull(id);
  }


  @Test
  @DisplayName("Should return user when token is valid and user exists")
  void testGetUserByTokenReturnsUser() {
    String token = jwt.generateJwtToken(user).getToken();

    // Spy to control extractIdFromJwt behavior
    JWT_token spyJwt = spy(jwt);
    doReturn(String.valueOf(user.getId())).when(spyJwt).extractIdFromJwt(token);
    when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));

    User result = spyJwt.getUserByToken(token);
    assertNotNull(result);
    assertEquals(user.getId(), result.getId());
  }

  @Test
  @DisplayName("Should return null when token is valid but user not found")
  void testGetUserByTokenReturnsNullWhenUserMissing() {
    String token = jwt.generateJwtToken(user).getToken();

    JWT_token spyJwt = spy(jwt);
    doReturn(String.valueOf(user.getId())).when(spyJwt).extractIdFromJwt(token);
    when(userRepo.findById(user.getId())).thenReturn(Optional.empty());

    User result = spyJwt.getUserByToken(token);
    assertNull(result);
  }

  @Test
  @DisplayName("Should return null when extractIdFromJwt returns null")
  void testGetUserByTokenReturnsNullOnInvalidToken() {
    JWT_token spyJwt = spy(jwt);
    doReturn(null).when(spyJwt).extractIdFromJwt("invalid.token");

    User result = spyJwt.getUserByToken("invalid.token");
    assertNull(result);
  }
  @Test
  @DisplayName("Should return admin user when token is valid and user exists")
  void testGetAdminUserByTokenReturnsUser() {
    String token = jwt.generateJwtToken(admin).getToken();

    JWT_token spyJwt = spy(jwt);
    doReturn(String.valueOf(admin.getId())).when(spyJwt).extractIdFromJwt(token);
    when(adminRepo.findById(admin.getId())).thenReturn(Optional.of(admin));

    Admin result = spyJwt.getAdminUserByToken(token);
    assertNotNull(result);
    assertEquals(admin.getId(), result.getId());
  }

  @Test
  @DisplayName("Should return null when token is valid but admin user not found")
  void testGetUserByTokenReturnsNullWhenAdminUserMissing() {
    String token = jwt.generateJwtToken(admin).getToken();

    JWT_token spyJwt = spy(jwt);
    doReturn(String.valueOf(admin.getId())).when(spyJwt).extractIdFromJwt(token);
    when(adminRepo.findById(admin.getId())).thenReturn(Optional.empty());

    Admin result = spyJwt.getAdminUserByToken(token);
    assertNull(result);
  }

  @Test
  @DisplayName("Should return null when extractIdFromJwt returns null")
  void testGetAdminUserByTokenReturnsNullOnInvalidToken() {
    JWT_token spyJwt = spy(jwt);
    doReturn(null).when(spyJwt).extractIdFromJwt("invalid.token");

    Admin result = spyJwt.getAdminUserByToken("invalid.token");
    assertNull(result);
  }
}
