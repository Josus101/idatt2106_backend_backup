package org.ntnu.idatt2106.backend.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.Console;
import org.ntnu.idatt2106.backend.dto.user.UserLoginRequest;
import org.ntnu.idatt2106.backend.dto.user.UserRegisterRequest;
import org.ntnu.idatt2106.backend.dto.user.UserTokenResponse;
import org.ntnu.idatt2106.backend.exceptions.AlreadyInUseException;
import org.ntnu.idatt2106.backend.exceptions.MailSendingFailedException;
import org.ntnu.idatt2106.backend.exceptions.UserNotFoundException;
import org.ntnu.idatt2106.backend.service.LoginService;
import org.ntnu.idatt2106.backend.service.ResetPasswordService;
import org.ntnu.idatt2106.backend.service.VerifyEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for handling user-related operations.
 * This class is responsible for defining the endpoints for user registration, login, password reset, and email verification.
 *
 * @version 1.0
 * @since 1.0
 * @Author Konrad Seime
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
  @Autowired
  private LoginService loginService;

  @Autowired
  private ResetPasswordService resetPasswordService;

  @Autowired
  private VerifyEmailService verifyEmailService;

  /**
   * Endpoint for registering a new user.
   * @param userRegister the user registration request containing email and password
   * @return a response entity indicating the result of the operation
   */
  @PostMapping("/register")
  @Operation(
      summary = "User register",
      description = "Endpoint for registering a new user with the given credentials"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "User registered successfully",
          content = @Content(
              schema = @Schema(example = "User registered successfully")
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid user data",
          content = @Content(
                  schema = @Schema(example = "Invalid user data")
          )
      ),
  })
  public ResponseEntity<String> registerUser(
    @RequestBody UserRegisterRequest userRegister) {
    try {
      loginService.register(userRegister);
      return ResponseEntity.ok("User registered successfully");
    }
    catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user data");
    } catch (AlreadyInUseException e) {
      return ResponseEntity.status(HttpStatus.IM_USED).body("Email already in use");
    } catch (MailSendingFailedException e) {
      System.out.println("Failed to send verification email. Either mail is invalid, or you're "
          + "missing .env file " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send verification email");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during registration");
    }
  }

  /**
   * Endpoint for user login.
   * @param userLogin the user login request containing email and password
   * @return a response entity containing the JWT token if login is successful
   */
  @PostMapping("/login")
  @Operation(
      summary = "User login",
      description = "Validates user credentials and returns a JWT token on success"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "JWT token returned",
          content = @Content(
              schema = @Schema(implementation = UserTokenResponse.class)
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "No user found with given email and password",
          content = @Content(
                  schema = @Schema(example = "Invalid user data")
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid user data",
          content = @Content(
              schema = @Schema(example = "No user found with given email and password")
          )
      )
  })
  public ResponseEntity<?> login(
      @RequestBody UserLoginRequest userLogin)
  {
    UserTokenResponse token;
    try {
      token = loginService.authenticate(userLogin.getEmail(), userLogin.getPassword());
    }
    catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user data");
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user found with given email and password");
    }
    return ResponseEntity.ok(token);
  }

  /**
   * Endpoint for resetting the password of a user.
   * @param token the token for password reset
   * @param newPassword the new password for the user
   * @return a response entity indicating the result of the operation
   */
  @GetMapping("/reset-password/{token}")
  @Operation(
      summary = "Reset password",
      description = "Resets the password for the user with the given token"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Password reset successfully"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "User not found with given token",
          content = @Content(
              schema = @Schema(implementation = String.class)
          )
      )
  })
  public ResponseEntity<String> resetPassword(
      @PathVariable String token,
      @RequestParam String newPassword) {
    try {
      resetPasswordService.resetPassword(token, newPassword);
      return ResponseEntity.ok("Password reset successfully");
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with given token");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid password");
    }
  }

  /**
   * Endpoint for verifying the email of a user.
   * @param token the token for email verification
   * @return a response entity indicating the result of the operation
   */
  @PutMapping("/verify/{token}")
  @Operation(
      summary = "Verify email",
      description = "Verifies the email for the user with the given token"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Email verified successfully"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "User not found with given token",
          content = @Content(
              schema = @Schema(implementation = String.class)
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid token",
          content = @Content(
              schema = @Schema(implementation = String.class)
          )
      )
  })
  public ResponseEntity<String> verifyEmail(
      @PathVariable String token) {
    try {
      verifyEmailService.verifyEmail(token);
      System.out.println("Email verified successfully");
      return ResponseEntity.ok("Email verified successfully");
    } catch (UserNotFoundException e) {
      System.out.println("User not found with given token");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with given token");
    } catch (IllegalArgumentException e) {
      System.out.println("Invalid token");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
    }
  }
}

