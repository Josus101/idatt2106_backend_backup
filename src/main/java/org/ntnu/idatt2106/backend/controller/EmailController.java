package org.ntnu.idatt2106.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.ntnu.idatt2106.backend.repo.UserRepo;
import org.ntnu.idatt2106.backend.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling email-related operations.
 * Provides endpoints for sending verification and password reset emails.
 * @author Konrad Seime
 */
@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Tag(name = "Email", description = "Endpoints for sending email verification and password reset links")
public class EmailController {

  private final EmailService emailService;
  private final UserRepo userRepo;

  /**
   * Endpoint for sending a verification email to a user.
   *
   * @param email the email of the user to send the verification email to
   * @return a response entity indicating the result of the operation
   */
  @PostMapping("/verify/{email}")
  @Operation(
      summary = "Send verification email",
      description = "Sends an email with a verification link to the user with the specified email.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Verification email sent",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(example = "Verification email sent"))
          ),
          @ApiResponse(
              responseCode = "400",
              description = "User is already verified",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(example = "Error: User is already verified"))
          ),
      }
  )
  public ResponseEntity<String> sendVerification(
      @Parameter(
          description = "email of the user",
          example = "krekardesign@gmail.com"
      ) @PathVariable String email
  ){
    System.out.println("Sending verification email to user with email: " + email);
    return userRepo.findByEmail(email)
        .map(user -> {
          try {
            emailService.sendVerificationEmail(user);
          } catch (MessagingException e) {
            throw new RuntimeException(e);
          } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body("Error: User is already verified");
          }
          return ResponseEntity.ok("Verification email sent");
        })
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Endpoint for sending a password reset email to a user.
   *
   * @param email the email of the user to send the password reset email to
   * @return a response entity indicating the result of the operation
   */
  @PostMapping("/reset-password/{email}")
  @Operation(
      summary = "Send reset password email",
      description = "Sends an email with a password reset link to the user with the specified email.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Reset password email sent",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(example = "Reset password email sent"))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "User not found",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(example = "Error: Email sender is not configured. Make sure you have the current .env file"))
          ),
      }
  )
  public ResponseEntity<String> sendResetPassword(
      @Parameter(
          description = "Email of the user",
          example = "ape@ape.com"
      ) @PathVariable String email
  ){
    System.out.println("Sending reset password email to user with email: " + email);
    return userRepo.findByEmail(email)
        .map(user -> {
          try {
            emailService.sendResetPasswordEmail(user);
          } catch (MessagingException e) {
            throw new RuntimeException(e);
          }
          return ResponseEntity.ok("Reset password email sent");
        })
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Endpoint for sending a test email to a specified address.
   *
   * @param to the recipient email address
   * @return a response entity indicating the result of the operation
   */
  @PostMapping("/test")
  @Operation(
      summary = "Send test email",
      description = "Sends a test email to a specified address for debugging purposes.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Test email sent",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(example = "Test Email sent to: example@email.com"))
          )
      }
  )
  public ResponseEntity<String> sendTestEmail(
      @Parameter(
          description = "Recipient email address",
          example = "example@mail.com"
      ) @RequestParam String to
  ){
    System.out.println("Sending test email to: " + to);
    try {
      emailService.sendTestEmail(to, "Test Email", "This is a test email from EmailService.");
    } catch (MessagingException e) {
      throw new RuntimeException("Failed to send test email", e);
    }
    return ResponseEntity.ok("Test email sent to: " + to);
  }
}
