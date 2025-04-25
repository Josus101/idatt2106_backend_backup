package org.ntnu.idatt2106.backend.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.ntnu.idatt2106.backend.dto.user.UserLoginRequest;
import org.ntnu.idatt2106.backend.dto.user.UserRegisterRequest;
import org.ntnu.idatt2106.backend.dto.user.UserTokenResponse;
import org.ntnu.idatt2106.backend.exceptions.UserNotFoundException;
import org.ntnu.idatt2106.backend.service.LoginService;
import org.ntnu.idatt2106.backend.service.ResetPasswordService;
import org.ntnu.idatt2106.backend.service.VerifyEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
  @Autowired
  private LoginService loginService;

  @Autowired
  private ResetPasswordService resetPasswordService;

  @Autowired
  private VerifyEmailService verifyEmailService;

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
    }
  }


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

  @GetMapping("/verify/{token}")
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
      )
  })
  public ResponseEntity<String> verifyEmail(
      @PathVariable String token) {
    try {
      verifyEmailService.verifyEmail(token);
      return ResponseEntity.ok("Email verified successfully");
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with given token");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
    }
  }
}

