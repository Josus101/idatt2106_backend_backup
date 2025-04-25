package org.ntnu.idatt2106.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.repo.UserRepo;
import org.ntnu.idatt2106.backend.service.EmailService;
import org.springframework.http.ResponseEntity;

import jakarta.mail.MessagingException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailControllerTest {

  @Mock
  private EmailService emailService;

  @Mock
  private UserRepo userRepo;

  @InjectMocks
  private EmailController emailController;

  private User testUser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    testUser = new User();
    testUser.setId(1);
    testUser.setEmail("test@example.com");
  }

  @Test
  @DisplayName("Should return 200 when verification email is sent successfully")
  void shouldSendVerificationEmail() throws Exception {
    when(userRepo.findById(1)).thenReturn(Optional.of(testUser));

    ResponseEntity<String> response = emailController.sendVerification(1);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals("Verification email sent.", response.getBody());
    verify(emailService).sendVerificationEmail(testUser);
  }

  @Test
  @DisplayName("Should return 404 when user not found for verification email")
  void shouldReturnNotFoundForVerification() throws MessagingException {
    when(userRepo.findById(1)).thenReturn(Optional.empty());

    ResponseEntity<String> response = emailController.sendVerification(1);

    assertEquals(404, response.getStatusCodeValue());
    verify(emailService, never()).sendVerificationEmail(any());
  }

  @Test
  @DisplayName("Should return 400 when user is already verified")
  void shouldReturnBadRequestWhenAlreadyVerified() throws Exception {
    when(userRepo.findById(1)).thenReturn(Optional.of(testUser));
    doThrow(new IllegalStateException("User is already verified."))
        .when(emailService).sendVerificationEmail(testUser);

    ResponseEntity<String> response = emailController.sendVerification(1);

    assertEquals(400, response.getStatusCodeValue());
    assertEquals("User is already verified.", response.getBody());
    verify(emailService).sendVerificationEmail(testUser);
  }

  @Test
  @DisplayName("Should return 200 when reset password email is sent successfully")
  void shouldSendResetPasswordEmail() throws Exception {
    when(userRepo.findById(1)).thenReturn(Optional.of(testUser));

    ResponseEntity<String> response = emailController.sendResetPassword(1);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals("Reset password email sent.", response.getBody());
    verify(emailService).sendResetPasswordEmail(testUser);
  }

  @Test
  @DisplayName("Should return 404 when user not found for password reset")
  void shouldReturnNotFoundForResetPassword() throws MessagingException {
    when(userRepo.findById(1)).thenReturn(Optional.empty());

    ResponseEntity<String> response = emailController.sendResetPassword(1);

    assertEquals(404, response.getStatusCodeValue());
    verify(emailService, never()).sendResetPasswordEmail(any());
  }

  @Test
  @DisplayName("Should send test email and return 200 OK")
  void shouldSendTestEmailSuccessfully() {
    ResponseEntity<String> response = emailController.sendTestEmail("test@example.com");

    assertEquals(200, response.getStatusCodeValue());
    assertEquals("Test email sent to: test@example.com", response.getBody());
    verify(emailService).sendTestEmail("test@example.com", "Test Email", "This is a test email from EmailService.");
  }

  @Test
  @DisplayName("Should throw RuntimeException when MessagingException occurs in verification email")
  void shouldThrowRuntimeExceptionForVerificationMessagingException() throws Exception {
    when(userRepo.findById(1)).thenReturn(Optional.of(testUser));
    doThrow(new MessagingException("Failed to send")).when(emailService).sendVerificationEmail(testUser);

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      emailController.sendVerification(1);
    });

    assertEquals("jakarta.mail.MessagingException: Failed to send", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw RuntimeException when MessagingException occurs in reset password email")
  void shouldThrowRuntimeExceptionForResetPasswordMessagingException() throws Exception {
    when(userRepo.findById(1)).thenReturn(Optional.of(testUser));
    doThrow(new MessagingException("Failed to send")).when(emailService).sendResetPasswordEmail(testUser);

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      emailController.sendResetPassword(1);
    });

    assertEquals("jakarta.mail.MessagingException: Failed to send", exception.getMessage());
  }
}
