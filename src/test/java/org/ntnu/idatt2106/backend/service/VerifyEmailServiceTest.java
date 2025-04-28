package org.ntnu.idatt2106.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.ntnu.idatt2106.backend.exceptions.TokenExpiredException;
import org.ntnu.idatt2106.backend.exceptions.UserNotFoundException;
import org.ntnu.idatt2106.backend.model.EmailVerifyToken;
import org.ntnu.idatt2106.backend.model.ResetPasswordToken;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.repo.EmailVerificationTokenRepo;
import org.ntnu.idatt2106.backend.repo.UserRepo;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VerifyEmailServiceTest {

  @InjectMocks
  private VerifyEmailService verifyEmailService;

  @Mock
  private EmailVerificationTokenRepo emailVerificationTokenRepo;

  @Mock
  private UserRepo userRepo;

  @Mock
  private LoginService loginService;

  private User testUser;
  private EmailVerifyToken validToken;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    testUser = new User();
    testUser.setId(1);
    testUser.setEmail("user@example.com");

    validToken = new EmailVerifyToken();
    validToken.setToken("valid-token");
    validToken.setUser(testUser);
    validToken.setExpirationDate(new Date(System.currentTimeMillis() + 3600000)); // 1 hour in future
  }

  @Test
  @DisplayName("Should return user when token is valid and not expired")
  void shouldReturnUserWhenTokenIsValid() {
    when(emailVerificationTokenRepo.findByToken("valid-token")).thenReturn(Optional.of(validToken));

    User result = verifyEmailService.findUserByToken("valid-token");

    assertNotNull(result);
    assertEquals(testUser, result);
  }

  @Test
  @DisplayName("Should throw UserNotFoundException when token does not exist")
  void shouldThrowWhenTokenDoesNotExist() {
    when(emailVerificationTokenRepo.findByToken("missing-token")).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> {
      verifyEmailService.findUserByToken("missing-token");
    });
  }

  @Test
  @DisplayName("Should throw TokenExpiredException when token is expired")
  void shouldThrowWhenTokenIsExpired() {
    validToken.setExpirationDate(new Date(System.currentTimeMillis() - 1000)); // expired
    when(emailVerificationTokenRepo.findByToken("expired-token")).thenReturn(Optional.of(validToken));

    assertThrows(TokenExpiredException.class, () -> {
      verifyEmailService.findUserByToken("expired-token");
    });
  }

  @Test
  @DisplayName("Should call LoginService to verify email if token is valid")
  void shouldCallLoginServiceWhenTokenIsValid() {
    when(emailVerificationTokenRepo.findByToken("valid-token")).thenReturn(Optional.of(validToken));

    verifyEmailService.verifyEmail("valid-token");

    verify(loginService).verifyEmail(testUser);
  }

  @Test
  @DisplayName("Should not verify email if token is missing")
  void shouldNotVerifyEmailWhenTokenIsMissing() {
    when(emailVerificationTokenRepo.findByToken("invalid-token")).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> {
      verifyEmailService.verifyEmail("invalid-token");
    });

    verify(loginService, never()).verifyEmail(any());
  }

  @Test
  @DisplayName("Should throw UserNotFoundException when user is null")
  void testVerifyEmailWhenUserNull() {
    String token = "valid-token";
    String newPassword = "newPassword123";

    EmailVerifyToken mockToken = mock(EmailVerifyToken.class);
    when(mockToken.getExpirationDate()).thenReturn(new Date(System.currentTimeMillis() + 10000)); // not expired
    when(mockToken.getUser()).thenReturn(null); // simulate missing user

    when(emailVerificationTokenRepo.findByToken(token)).thenReturn(Optional.of(mockToken));

    assertThrows(UserNotFoundException.class, () -> verifyEmailService.verifyEmail(token));
  }
}
