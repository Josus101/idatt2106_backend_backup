package org.ntnu.idatt2106.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.ntnu.idatt2106.backend.exceptions.UserNotFoundException;
import org.ntnu.idatt2106.backend.model.ResetPasswordToken;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.repo.ResetPasswordTokenRepo;
import org.ntnu.idatt2106.backend.repo.UserRepo;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ResetPasswordServiceTest {

  @InjectMocks
  private ResetPasswordService resetPasswordService;

  @Mock
  private ResetPasswordTokenRepo resetPasswordTokenRepo;

  @Mock
  private UserRepo userRepo;

  @Mock
  private LoginService loginService;

  private User testUser;
  private ResetPasswordToken validToken;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    testUser = new User();
    testUser.setId(1);
    testUser.setEmail("user@example.com");

    validToken = new ResetPasswordToken();
    validToken.setToken("valid-token");
    validToken.setUser(testUser);
    validToken.setExpirationDate(new Date(System.currentTimeMillis() + 3600000)); // 1 hour
  }

  @Test
  @DisplayName("Should return user when token is valid and not expired")
  void shouldReturnUserWhenTokenIsValid() {
    when(resetPasswordTokenRepo.findByToken("valid-token")).thenReturn(Optional.of(validToken));

    User foundUser = resetPasswordService.findUserByToken("valid-token");

    assertNotNull(foundUser);
    assertEquals(testUser, foundUser);
  }

  @Test
  @DisplayName("Should throw UserNotFoundException when token is missing")
  void shouldThrowWhenTokenMissing() {
    when(resetPasswordTokenRepo.findByToken("missing-token")).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> {
      resetPasswordService.findUserByToken("missing-token");
    });
  }

  @Test
  @DisplayName("Should throw UserNotFoundException when token is expired")
  void shouldThrowWhenTokenExpired() {
    validToken.setExpirationDate(new Date(System.currentTimeMillis() - 1000)); // expired
    when(resetPasswordTokenRepo.findByToken("expired-token")).thenReturn(Optional.of(validToken));

    assertThrows(UserNotFoundException.class, () -> {
      resetPasswordService.findUserByToken("expired-token");
    });
  }

  @Test
  @DisplayName("Should call LoginService to reset password when token is valid")
  void shouldCallLoginServiceOnValidToken() {
    when(resetPasswordTokenRepo.findByToken("valid-token")).thenReturn(Optional.of(validToken));

    resetPasswordService.resetPassword("valid-token", "newPassword");

    verify(loginService).resetPassword(testUser, "newPassword");
  }

  @Test
  @DisplayName("Should throw UserNotFoundException and not reset password when token is invalid")
  void shouldNotResetPasswordWhenTokenInvalid() {
    when(resetPasswordTokenRepo.findByToken("invalid-token")).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> {
      resetPasswordService.resetPassword("invalid-token", "newPassword");
    });

    verify(loginService, never()).resetPassword(any(), any());
  }
}
