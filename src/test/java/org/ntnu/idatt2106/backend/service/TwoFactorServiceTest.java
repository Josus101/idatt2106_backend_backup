package org.ntnu.idatt2106.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.ntnu.idatt2106.backend.model.VerificationToken;
import org.ntnu.idatt2106.backend.model.VerificationTokenType;
import org.ntnu.idatt2106.backend.repo.VerificationTokenRepo;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TwoFactorServiceTest {

  @InjectMocks
  private TwoFactorService twoFactorService;

  @Mock
  private VerificationTokenRepo verificationTokenRepo;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("verifyTokenNotInUse returns true for non-existing token")
  void testVerifyTokenNotInUseTrue() {
    when(verificationTokenRepo.findByToken("ABC123")).thenReturn(Optional.empty());
    assertTrue(twoFactorService.verifyTokenNotInUse("ABC123"));
  }

  @Test
  @DisplayName("verifyTokenNotInUse returns true if token is expired")
  void testVerifyTokenNotInUseExpired() {
    VerificationToken token = new VerificationToken(
        "ABC123", "email@example.com",
        new Date(System.currentTimeMillis() - 10000),
        VerificationTokenType.TWO_FACTOR_AUTHENTICATION
    );
    when(verificationTokenRepo.findByToken("ABC123")).thenReturn(Optional.of(token));
    assertTrue(twoFactorService.verifyTokenNotInUse("ABC123"));
  }

  @Test
  @DisplayName("verifyTokenNotInUse returns false if token is of wrong type")
  void testVerifyTokenNotInUseWrongType() {
    VerificationToken token = new VerificationToken(
        "ABC123", "email@example.com",
        new Date(System.currentTimeMillis() + 10000),
        VerificationTokenType.PASSWORD_RESET
    );
    when(verificationTokenRepo.findByToken("ABC123")).thenReturn(Optional.of(token));
    assertFalse(twoFactorService.verifyTokenNotInUse("ABC123"));
  }

  @Test
  @DisplayName("generate2FA_Token returns valid unique token")
  void testGenerate2FATokenReturnsToken() {
    when(verificationTokenRepo.findByToken(anyString())).thenReturn(Optional.empty());
    String token = twoFactorService.generate2FA_Token(10);
    assertNotNull(token);
    assertEquals(6, token.length());
  }

  @Test
  @DisplayName("generate2FA_Token returns null if no unique token is found")
  void testGenerate2FATokenReturnsNull() {
    VerificationToken token = new VerificationToken("ABC123", "email", new Date(System.currentTimeMillis() + 10000), VerificationTokenType.TWO_FACTOR_AUTHENTICATION);
    when(verificationTokenRepo.findByToken(anyString())).thenReturn(Optional.of(token));

    String result = twoFactorService.generate2FA_Token(10);
    assertNull(result);
  }

  @Test
  @DisplayName("create2FA_Token stores and returns a valid token")
  void testCreate2FAToken() {
    when(verificationTokenRepo.findByToken(anyString())).thenReturn(Optional.empty());
    String token = twoFactorService.create2FA_Token("email@example.com");

    assertNotNull(token);
    verify(verificationTokenRepo).save(any(VerificationToken.class));
  }

  @Test
  @DisplayName("create2FA_Token throws if no unique token is generated")
  void testCreate2FATokenThrows() {
    VerificationToken existingToken = new VerificationToken("ABC123", "email", new Date(System.currentTimeMillis() + 10000), VerificationTokenType.TWO_FACTOR_AUTHENTICATION);
    when(verificationTokenRepo.findByToken(anyString())).thenReturn(Optional.of(existingToken));

    TwoFactorService spyService = Mockito.spy(twoFactorService);
    doReturn(null).when(spyService).generate2FA_Token(anyInt());

    assertThrows(IllegalStateException.class, () -> {
      spyService.create2FA_Token("email@example.com");
    });
  }

  @Test
  @DisplayName("validate2FA_Token returns true for valid token")
  void testValidate2FATokenTrue() {
    VerificationToken token = new VerificationToken(
        "ABC123", "email@example.com",
        new Date(System.currentTimeMillis() + 10000),
        VerificationTokenType.TWO_FACTOR_AUTHENTICATION
    );
    when(verificationTokenRepo.findByToken("ABC123")).thenReturn(Optional.of(token));
    assertTrue(twoFactorService.validate2FA_Token("ABC123"));
  }

  @Test
  @DisplayName("validate2FA_Token returns false for invalid or expired token")
  void testValidate2FATokenFalse() {
    VerificationToken token = new VerificationToken(
        "ABC123", "email@example.com",
        new Date(System.currentTimeMillis() - 10000),
        VerificationTokenType.TWO_FACTOR_AUTHENTICATION
    );
    when(verificationTokenRepo.findByToken("ABC123")).thenReturn(Optional.of(token));
    assertFalse(twoFactorService.validate2FA_Token("ABC123"));
  }

  @Test
  @DisplayName("getAdminUserByToken returns email if token is valid")
  void testGetAdminUserByTokenSuccess() {
    VerificationToken token = new VerificationToken(
        "ABC123", "admin@example.com",
        new Date(System.currentTimeMillis() + 10000),
        VerificationTokenType.TWO_FACTOR_AUTHENTICATION
    );
    when(verificationTokenRepo.findByToken("ABC123")).thenReturn(Optional.of(token));
    assertEquals("admin@example.com", twoFactorService.getAdminUserByToken("ABC123"));
  }

  @Test
  @DisplayName("getAdminUserByToken throws if token not found")
  void testGetAdminUserByTokenThrowsNotFound() {
    when(verificationTokenRepo.findByToken("XYZ789")).thenReturn(Optional.empty());
    assertThrows(IllegalStateException.class, () -> {
      twoFactorService.getAdminUserByToken("XYZ789");
    });
  }

  @Test
  @DisplayName("getAdminUserByToken throws if token is wrong type")
  void testGetAdminUserByTokenThrowsWrongType() {
    VerificationToken token = new VerificationToken(
        "XYZ789", "admin@example.com",
        new Date(System.currentTimeMillis() + 10000),
        VerificationTokenType.PASSWORD_RESET
    );
    when(verificationTokenRepo.findByToken("XYZ789")).thenReturn(Optional.of(token));
    assertThrows(IllegalArgumentException.class, () -> {
      twoFactorService.getAdminUserByToken("XYZ789");
    });
  }
}
