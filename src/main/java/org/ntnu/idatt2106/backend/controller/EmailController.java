package org.ntnu.idatt2106.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.repo.UserRepo;
import org.ntnu.idatt2106.backend.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Tag(name = "Email", description = "Endpoints for sending email verification and password reset links")
public class EmailController {

  private final EmailService emailService;
  private final UserRepo userRepo;

  @Operation(
      summary = "Send verification email",
      description = "Sends an email with a verification link to the user with the specified ID.",
      responses = {
          @ApiResponse(responseCode = "200", description = "Verification email sent"),
          @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
      }
  )
  @PostMapping("/verify/{userId}")
  public ResponseEntity<String> sendVerification(
      @Parameter(description = "ID of the user", example = "1")
      @PathVariable int userId) {
    System.out.println("Sending verification email to user with ID: " + userId);
    return userRepo.findById(userId)
        .map(user -> {
          try {
            emailService.sendVerificationEmail(user);
          } catch (MessagingException e) {
            throw new RuntimeException(e);
          }
          return ResponseEntity.ok("Verification email sent.");
        })
        .orElse(ResponseEntity.notFound().build());
  }

  @Operation(
      summary = "Send reset password email",
      description = "Sends an email with a password reset link to the user with the specified ID.",
      responses = {
          @ApiResponse(responseCode = "200", description = "Reset password email sent"),
          @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
      }
  )
  @PostMapping("/reset-password/{userId}")
  public ResponseEntity<String> sendResetPassword(
      @Parameter(description = "ID of the user", example = "1")
      @PathVariable int userId) {
    System.out.println("Sending reset password email to user with ID: " + userId);
    return userRepo.findById(userId)
        .map(user -> {
          try {
            emailService.sendResetPasswordEmail(user);
          } catch (MessagingException e) {
            throw new RuntimeException(e);
          }
          return ResponseEntity.ok("Reset password email sent.");
        })
        .orElse(ResponseEntity.notFound().build());
  }

  @Operation(
      summary = "Send test email",
      description = "Sends a test email to a specified address for debugging purposes.",
      responses = {
          @ApiResponse(responseCode = "200", description = "Test email sent")
      }
  )
  @PostMapping("/test")
  public ResponseEntity<String> sendTestEmail(
      @Parameter(description = "Recipient email address", example = "example@mail.com")
      @RequestParam String to) {
    System.out.println("Sending test email to: " + to);
    emailService.sendTestEmail(to, "Test Email", "This is a test email from EmailService.");
    return ResponseEntity.ok("Test email sent to: " + to);
  }
}
