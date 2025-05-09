package org.ntnu.idatt2106.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ntnu.idatt2106.backend.exceptions.TokenExpiredException;
import org.ntnu.idatt2106.backend.exceptions.UserNotFoundException;
import org.ntnu.idatt2106.backend.model.Admin;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.model.VerificationToken;
import org.ntnu.idatt2106.backend.model.VerificationTokenType;
import org.ntnu.idatt2106.backend.repo.AdminRepo;
import org.ntnu.idatt2106.backend.repo.UserRepo;
import org.ntnu.idatt2106.backend.repo.VerificationTokenRepo;

/**
 * Unit tests for the VerificationService class.
 * This class tests the functionality of the VerificationService class,
 * including methods for verifying email, resetting password, and finding users by token.
 */
public class VerificationServiceTest {


  @InjectMocks
  private VerificationService verificationService;

  @Mock
  private VerificationTokenRepo verificationTokenRepo;

  @Mock
  private UserRepo userRepo;

  @Mock
  private LoginService loginService;

  @Mock
  private AdminRepo adminRepo;

  @Mock
  private AdminService adminService;

  private User testUser;
  private VerificationToken validEmailToken;
  private VerificationToken validPasswordToken;
  private VerificationToken validAdminToken;
  private Admin admin;


  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    testUser = new User();
    testUser.setId(1);
    testUser.setEmail("user@example.com");
    
    admin = new Admin();
    admin.setId(1);
    admin.setEmail("admin@admin.com");

    validEmailToken = new VerificationToken();
    validEmailToken.setToken("valid-token");
    validEmailToken.setEmail(testUser.getEmail());
    validEmailToken.setExpirationDate(new Date(System.currentTimeMillis() + 3600000)); // 1 hour in future
    validEmailToken.setType(VerificationTokenType.EMAIL_VERIFICATION);
    
    validAdminToken = new VerificationToken();
    validAdminToken.setToken("valid-admin-token");
    validAdminToken.setEmail(admin.getEmail());
    validAdminToken.setExpirationDate(new Date(System.currentTimeMillis() + 3600000)); // 1 hour in future
    validAdminToken.setType(VerificationTokenType.ADMIN_VERIFICATION);

    validPasswordToken = new VerificationToken();
    validPasswordToken.setToken("valid-password-token");
    validPasswordToken.setEmail(testUser.getEmail());
    validPasswordToken.setExpirationDate(new Date(System.currentTimeMillis() + 3600000)); // 1 hour in future
    validPasswordToken.setType(VerificationTokenType.PASSWORD_RESET);
  }

  @Test
  @DisplayName("Should throw UserNotFoundException when token does not exist")
  void shouldThrowWhenTokenDoesNotExist() {
    when(verificationTokenRepo.findByToken("missing-token")).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> {
      verificationService.findUserByToken("missing-token", VerificationTokenType.EMAIL_VERIFICATION);
    });
  }

  @Test
  @DisplayName("Should throw TokenExpiredException when token is expired")
  void shouldThrowWhenTokenIsExpired() {
    validEmailToken.setExpirationDate(new Date(System.currentTimeMillis() - 1000)); // expired
    when(verificationTokenRepo.findByToken("expired-token")).thenReturn(Optional.of(validEmailToken));

    assertThrows(TokenExpiredException.class, () -> {
      verificationService.findUserByToken("expired-token", VerificationTokenType.EMAIL_VERIFICATION);
    });
  }

  @Test
  @DisplayName("Should call LoginService to verify email if token is valid")
  void shouldCallLoginServiceWhenTokenIsValid() {
    when(verificationTokenRepo.findByToken("valid-token")).thenReturn(Optional.of(validEmailToken));
    when(userRepo.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
    verificationService.verifyEmail("valid-token");

    verify(loginService).verifyEmail(testUser);
  }

  @Test
  @DisplayName("Should not verify email if token is missing")
  void shouldNotVerifyEmailWhenTokenIsMissing() {
    when(verificationTokenRepo.findByToken("invalid-token")).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> {
      verificationService.verifyEmail("invalid-token");
    });

    verify(loginService, never()).verifyEmail(any());
  }

  @Test
  @DisplayName("Should throw UserNotFoundException when user is null")
  void testVerifyEmailWhenUserNull() {
    String token = "valid-token";

    when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> verificationService.verifyEmail(token));
  }


  @Test
  @DisplayName("Should return user when token is valid and not expired")
  void shouldReturnUserWhenTokenIsValid() {
    when(verificationTokenRepo.findByToken("valid-token")).thenReturn(Optional.of(validEmailToken));
    when(userRepo.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

    User foundUser = verificationService.findUserByToken("valid-token", VerificationTokenType.EMAIL_VERIFICATION);

    assertNotNull(foundUser);
    assertEquals(testUser, foundUser);
  }

  @Test
  @DisplayName("Should throw UserNotFoundException when token is missing")
  void shouldThrowWhenTokenMissing() {
    when(verificationTokenRepo.findByToken("missing-token")).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> {
      verificationService.findUserByToken("missing-token", VerificationTokenType.EMAIL_VERIFICATION);
    });
  }

  @Test
  @DisplayName("Should throw TokenExpiredException when token is expired")
  void shouldThrowWhenTokenExpired() {
    validEmailToken.setExpirationDate(new Date(System.currentTimeMillis() - 1000)); // expired
    when(verificationTokenRepo.findByToken("expired-token")).thenReturn(Optional.of(validEmailToken));

    assertThrows(TokenExpiredException.class, () -> {
      verificationService.findUserByToken("expired-token", VerificationTokenType.EMAIL_VERIFICATION);
    });
  }

  @Test
  @DisplayName("Should call LoginService to reset password when token is valid")
  void shouldCallLoginServiceOnValidToken() {
    when(verificationTokenRepo.findByToken("valid-token")).thenReturn(Optional.of(validPasswordToken));
    when(userRepo.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

    verificationService.resetPassword("valid-token", "newPassword");

    verify(loginService).resetPassword(testUser, "newPassword");
  }

  @Test
  @DisplayName("Should throw UserNotFoundException and not reset password when token is invalid")
  void shouldNotResetPasswordWhenTokenInvalid() {
    when(verificationTokenRepo.findByToken("invalid-token")).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> {
      verificationService.resetPassword("invalid-token", "newPassword");
    });

    verify(loginService, never()).resetPassword(any(), any());
  }

  @Test
  @DisplayName("resetPassword throws UserNotFoundException when user is null")
  void testResetPasswordUserNull() {
    String token = "valid-token";
    String newPassword = "newPassword123";

    VerificationToken mockToken = mock(VerificationToken.class);
    when(mockToken.getExpirationDate()).thenReturn(new Date(System.currentTimeMillis() + 10000)); // not expired
    when(mockToken.getEmail()).thenReturn(null);

    when(verificationTokenRepo.findByToken(token)).thenReturn(Optional.of(mockToken));
    when(userRepo.findByEmail(mockToken.getEmail())).thenReturn(Optional.empty());
    when(mockToken.getType()).thenReturn(VerificationTokenType.PASSWORD_RESET);

    assertThrows(UserNotFoundException.class, () -> verificationService.resetPassword(token, newPassword));
  }

  @Test
  @DisplayName("findAdminByToken returns admin when token is valid")
  void testFindAdminByToken() {
    when(verificationTokenRepo.findByToken("valid-admin-token")).thenReturn(Optional.of(validAdminToken));
    when(adminRepo.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));

    Admin foundAdmin = verificationService.findAdminByToken("valid-admin-token", VerificationTokenType.ADMIN_VERIFICATION);

    assertNotNull(foundAdmin);
    assertEquals(admin, foundAdmin);
  }

  @Test
  @DisplayName("findAdminByToken throws UserNotFoundException when token is missing")
  void testFindAdminByTokenMissing() {
    when(verificationTokenRepo.findByToken("missing-token")).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> {
      verificationService.findAdminByToken("missing-token", VerificationTokenType.ADMIN_VERIFICATION);
    });
  }

  @Test
  @DisplayName("findAdminByToken throws UserNotFoundException when email not found")
  void testFindAdminByTokenEmailNotFound() {
    when(verificationTokenRepo.findByToken("valid-admin-token")).thenReturn(Optional.of(validAdminToken));
    when(adminRepo.findByEmail("unknown-email")).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> {
      verificationService.findAdminByToken("valid-admin-token", VerificationTokenType.ADMIN_VERIFICATION);
    });
  }

  @Test
  @DisplayName("findAdminByToken throws UserNotFoundException when token is null")
  void testFindAdminByTokenNull() {
    assertThrows(UserNotFoundException.class, () -> {
      verificationService.findAdminByToken(null, VerificationTokenType.ADMIN_VERIFICATION);
    });
  }

  @Test
  @DisplayName("findAdminByToken throws UserNotFoundException when admin not found with valid token")
  void testFindAdminByTokenAdminNotFound() {
    when(verificationTokenRepo.findByToken("valid-token")).thenReturn(Optional.of(validAdminToken));
    when(adminRepo.findByEmail("non-existent-email")).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> {
      verificationService.findAdminByToken("valid-token", VerificationTokenType.ADMIN_VERIFICATION);
    });
  }

  @Test
  @DisplayName("findAdminByToken throws UserNotFoundException for invalid token type")
  void testFindAdminByTokenInvalidType() {
    when(verificationTokenRepo.findByToken("valid-token")).thenReturn(Optional.of(validAdminToken));
    when(adminRepo.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

    assertThrows(UserNotFoundException.class, () -> {
      verificationService.findAdminByToken("valid-token", VerificationTokenType.ADMIN_VERIFICATION);
    });
  }


  @Test
  @DisplayName("activateAdmin activates admin when token is valid")
  void testActivateAdmin() {
    when(verificationTokenRepo.findByToken("valid-admin-token")).thenReturn(Optional.of(validAdminToken));
    when(adminRepo.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
    verificationService.activateAdmin("valid-admin-token","pass");
  }

  @Test
  @DisplayName("activateAdmin throws UserNotFoundException when token is missing")
  void testActivateAdminMissing() {
    when(verificationTokenRepo.findByToken("missing-token")).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> {
      verificationService.activateAdmin("missing-token", "pass");
    });
  }

  @Test
  @DisplayName("activateAdmin throws when admin already activated")
  void testActivateAdminAlreadyActivated() {
    when(verificationTokenRepo.findByToken("valid-admin-token")).thenReturn(Optional.of(validAdminToken));
    when(adminRepo.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
    admin.setActive(true);
    assertThrows(IllegalStateException.class, () -> {
      verificationService.activateAdmin("valid-admin-token", "pass");
    });
  }

  @Test
  @DisplayName("findEmailByToken throws IllegalArgumentException when not token type matche provided type")
  void testFindEmailByTokenTypeMismatch() {
    when(verificationTokenRepo.findByToken("valid-token")).thenReturn(Optional.of(validEmailToken));

    assertThrows(IllegalArgumentException.class, () -> {
      verificationService.findEmailByToken("valid-token", VerificationTokenType.PASSWORD_RESET);
    });
  }



}
