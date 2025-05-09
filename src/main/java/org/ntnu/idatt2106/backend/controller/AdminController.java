package org.ntnu.idatt2106.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.ntnu.idatt2106.backend.dto.admin.AdminGetResponse;
import org.ntnu.idatt2106.backend.dto.admin.AdminLoginRequest;
import org.ntnu.idatt2106.backend.dto.admin.AdminRegisterRequest;
import org.ntnu.idatt2106.backend.dto.news.NewsGetResponse;
import org.ntnu.idatt2106.backend.dto.user.EmailRequest;
import org.ntnu.idatt2106.backend.exceptions.UnauthorizedException;
import org.ntnu.idatt2106.backend.exceptions.UserNotFoundException;
import org.ntnu.idatt2106.backend.exceptions.UserNotVerifiedException;
import org.ntnu.idatt2106.backend.service.AdminService;
import org.ntnu.idatt2106.backend.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This class is used to handle all the requests related to admin users.
 * It contains endpoints for creating, deleting, and elevating admin users.
 *
 * @Author Konrad Seime
 * @since 0.1
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

  @Autowired
  private AdminService adminService;

  @Autowired
  private VerificationService verificationService;


  /**
   * Endpoint for creating a new admin user.
   * @param admin the admin user to be created
   * @param authorizationHeader the authorization header containing the JWT token
   * @return a response entity containing the result of the operation
   */
  @PostMapping("/createAdmin")
  @Operation(
      summary = "Create a new admin user",
      description = "Post request to create a new admin user. "
          + "Requires superuser privileges. "
          + "Returns true if the admin was created successfully, false otherwise."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Admin user created successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "true")
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid request",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "false")
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "false")
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal Server Error",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "false")
          )
      )
  })
  public ResponseEntity<Boolean> addAdminUser(
      @Parameter(
          name = "Authorization",
          description = "Bearer token in the format `Bearer <JWT>`",
          required = true,
          example = "Bearer eyJhbGciOiJIUzI1N.iIsInR5cCI6IkpXVCJ9..."
      )
      @RequestBody AdminRegisterRequest admin,
      @RequestHeader("Authorization") String authorizationHeader) {

    try {
      adminService.register(admin.getUsername(), admin.getEmail(), authorizationHeader);
    } catch (UnauthorizedException e) {

      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    } catch (Exception e) {

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
    }
    return ResponseEntity.ok(true);
  }

  /**
   * Endpoint for elevating an admin user to a superuser.
   * @param id the ID of the admin user to be elevated
   * @param authorizationHeader the authorization header containing the JWT token
   * @return a response entity containing the result of the operation
   */
  @PutMapping("/elevate/{id}")
  @Operation(
      summary = "Elevates an admin user to a superuseer",
      description = "Post request to elevate an admin user to a superuser. "
          + "Requires superuser privileges. "
          + "Returns true if the admin was elevated successfully, false otherwise."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Admin user elevated successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "true")
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid request",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "false")
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "false")
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal Server Error",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "false")
          )
      )
  })
  public ResponseEntity<Boolean> elevateAdminUser(
          @Parameter(
              name = "id",
              description = "The ID of the admin user to be elevated",
              required = true,
              example = "123")
          @PathVariable String id,
          @Parameter(
              name = "authorizationHeader",
              description = "Bearer token in the format `Bearer <JWT>`",
              required = true,
              example = "Bearer eyJhbGciOiJIUzI1N.iIsInR5cCI6IkpXVCJ9..."
      )
      @RequestHeader("Authorization") String authorizationHeader) {
    try {
      adminService.elevateAdmin(id, authorizationHeader);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
    } catch (UnauthorizedException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
    }
    return ResponseEntity.ok(true);
  }

  /**
   * Endpoint for logging in an admin user.
   * @param adminLogin the login request containing the username and password
   * @return a response entity containing the JWT token if successful
   */
  @PostMapping("/login")
  @Operation(
      summary = "Admin login",
      description = "Validates admin credentials and returns a JWT token on success"
  )
  @ApiResponses (value = {
      @ApiResponse(
          responseCode = "200",
          description = "JWT token returned upon successful authentication",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  type = "string",
                  example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
              )
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Error: Invalid admin data",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Invalid admin data")
          )
      ),
      @ApiResponse(
              responseCode = "404",
              description = "Error: No admin found with given username and password",
              content = @Content(
                      mediaType = "application/json",
                      schema = @Schema(example = "Error: No admin found with given username and password")
              )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Error: An unexpected error occurred",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: An unexpected error occurred")
          )
      )
  })
  public ResponseEntity<?> login(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Admin login request containing username and password",
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = AdminLoginRequest.class)
          )
      )
      @RequestBody AdminLoginRequest adminLogin) {
    try {
      String token;
      if (adminLogin.getTwoFactorCode() == null) {
        token = adminService.authenticate(adminLogin.getUsername(), adminLogin.getPassword());
      }
      else {
        token = adminService.authenticate(adminLogin.getUsername(), adminLogin.getPassword(), adminLogin.getTwoFactorCode());
      }
      return ResponseEntity.ok(token);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    } catch (UserNotFoundException e) {

      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
    } catch (UserNotVerifiedException e) {

      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: " + e.getMessage() + ". Please activate your account via the email.");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: An unexpected error occurred");
    }
  }

  /**
   * Endpoint for deleting an admin user.
   * @param id the ID of the admin user to be deleted
   * @param authorizationHeader the authorization header containing the JWT token
   * @return a response entity containing the result of the operation
   */
  @DeleteMapping("/delete/{id}")
  @Operation(
      summary = "Delete an admin user",
      description = "Post request to delete an admin user. "
          + "Requires superuser privileges. "
          + "Returns true if the admin was deleted successfully, false otherwise."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Admin user deleted successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "true")
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid request",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "false")
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "false")
          )
      ),
      @ApiResponse(
        responseCode = "500",
        description = "Internal Server Error",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(type = "boolean", example = "false")
        )
      )
  })
  public ResponseEntity<?> deleteAdminUser(
      @Parameter(
          name = "id",
          description = "The ID of the admin user to be deleted",
          required = true,
          example = "1"
      ) @PathVariable String id,
      @Parameter(
          name = "Authorization",
          description = "Bearer token in the format `Bearer <JWT>`",
          required = true,
          example = "Bearer eyJhbGciOiJIUzI1N.iIsInR5cCI6IkpXVCJ9..."
      )
      @RequestHeader("Authorization") String authorizationHeader) {
    try {
      adminService.exterminateAdmin(id, authorizationHeader);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
    } catch (UnauthorizedException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
    }
    return ResponseEntity.ok(true);
  }

  /**
   * Endpoint for getting all admin users.
   *
   * @param authorizationHeader the authorization header containing the JWT token
   * @return a response entity containing the list of admin users
   */
  @GetMapping("")
  @Operation(
      summary = "Get all admin users",
      description = "Get request to retrieve all admin users. "
          + "Returns a list of admin users."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "List of admin users",
          content = @Content(
              mediaType = "application/json",
              array = @ArraySchema(
                      schema = @Schema(implementation = AdminGetResponse.class)
              )
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "false")
          )
      ),
      @ApiResponse(
        responseCode = "404",
        description = "No admin users found",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(example = "Error: No admins found")
        )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal Server Error",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "false")
          )
      )
  })
  public ResponseEntity<?> getAllAdmins (
          @Parameter(
                  name = "Authorization",
                  description = "Bearer token in the format `Bearer <JWT>`",
                  required = true,
                  example = "Bearer eyJhbGciOiJIUzI1N.iIsInR5cCI6IkpXVCJ9..."
          ) @RequestHeader("Authorization") String authorizationHeader
  ) {
    try {
      return ResponseEntity.ok(adminService.getAllAdmins(authorizationHeader));
    } catch (UnauthorizedException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
    }
  }

    /**
     * Activates the admin account using the provided token.
     *
     * @param token The token to activate the admin account.
     * @param newPassword The new password to set for the admin account.
     * @return a response entity containing the result of the operation
     */
  @PutMapping("/activate/{token}")
  @Operation(
      summary = "Activate an admin account",
      description = "Put request to activate an admin account. "
          + "Returns true if the admin was activated successfully, false otherwise."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Admin account activated successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "true")
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid request",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "false")
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "false")
          )
      ),
      @ApiResponse(
        responseCode = "500",
        description = "Internal Server Error",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(type = "boolean", example = "false")
        )
      )
  })
  public ResponseEntity<?> activateAdmin(
      @Parameter(
          name = "token",
          description = "The token to activate the admin account",
          required = true,
          example = "eyJhbGciOiJIUzI1N.iIsInR5cCI6IkpXVCJ9..."
      )
      @PathVariable String token,
      @Parameter(
          name = "newPassword",
          description = "The new password to set for the admin account",
          required = true,
          example = "newpassword123"
      )
      @RequestParam String newPassword)
    {
      try {
        verificationService.activateAdmin(token, newPassword);
      } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
      } catch (UnauthorizedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
      } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
      }
      return ResponseEntity.ok(true);
    }

  /**
   * Sends 2fa code to the admin email.
   *
   * @param email The email of the admin to send the 2fa code to.
   * @return a response entity containing the result of the operation
   */
  @PostMapping("/2fa")
  @Operation(
      summary = "Send 2FA code to admin email",
      description = "Post request to send 2FA code to admin email. "
          + "Returns true if the 2FA code was sent successfully, false otherwise."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "2FA code sent successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "true")
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid request",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "Error: Invalid admin data")
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "Error: Unauthorized")
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Error: No admin found with given email",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "Error: No admin found with given email")
          )
      ),
      @ApiResponse(
        responseCode = "500",
        description = "Internal Server Error",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(type = "boolean", example = "Error: An unexpected error occurred")
        )
      )
  })
  public ResponseEntity<?> sendMeTheCode(
      @Parameter(
          name= "email",
          description = "The email of the admin to send the 2FA code to",
          required = true
      ) @RequestBody EmailRequest email) {
    try {
      adminService.send2FAToken(email.getEmail());
      return ResponseEntity.ok(true);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid admin data");
    } catch (UnauthorizedException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: " + e.getMessage());
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage() + ". Please check your email.");
    } catch (UserNotVerifiedException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: " + e.getMessage() + ". Please activate your account via the email.");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: An unexpected error occurred." + e.getMessage() + ". Please try again.");
    }
  }

  /**
   * Resets the password for the admin account using the provided token.
   *
   * @param token The token to reset password of the admin account.
   * @param newPassword The new password to set for the admin account.
   * @return a response entity containing the result of the operation
   */
  @PutMapping("/reset-password/{token}")
  @Operation(
      summary = "Reset admin password",
      description = "Put request to reset admin password. "
          + "Returns true if the password was reset successfully, false otherwise."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Admin account password reset successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "true")
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid request",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "false")
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "false")
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal Server Error",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "false")
          )
      )
  })
  public ResponseEntity<?> resetPasswordAdmin(
      @Parameter(
          name = "token",
          description = "The token to activate the admin account",
          required = true,
          example = "eyJhbGciOiJIUzI1N.iIsInR5cCI6IkpXVCJ9..."
      )
      @PathVariable String token,
      @Parameter(
          name = "newPassword",
          description = "The new password to set for the admin account",
          required = true,
          example = "newpassword123"
      )
      @RequestParam String newPassword)
  {
    try {
      verificationService.changeAdminPassword(token, newPassword);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage() + ". Please try again.");
    } catch (UnauthorizedException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: An unexpected error occurred." + e.getMessage());
    }
    return ResponseEntity.ok(true);
  }

  /**
   * Endpoint for sending a password reset email to the admin user.
   * Requires the admin's email address, and superuser privileges.
   *
   * @param emailRequest the email request containing the admin's email address
   * @param authorizationHeader the authorization header containing the JWT token
   * @return a response entity containing the list of news
   */
  @PostMapping("/reset-password")
  @Operation(
      summary = "Send password reset email to admin user",
      description = "Post request to send a password reset email to the admin user. "
          + "Requires superuser privileges. "
          + "Returns true if the email was sent successfully, false otherwise."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Password reset email sent successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "true")
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid request",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "false")
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(type = "boolean", example = "false")
          )
      ),
      @ApiResponse(
        responseCode = "500",
        description = "Internal Server Error",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(type = "boolean", example = "false")
        )
      )
  })
  public ResponseEntity<?> sendPasswordResetEmail(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Email request containing the admin's email address",
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = EmailRequest.class)
          )
      )
      @RequestBody EmailRequest emailRequest,
      @Parameter(
          name = "Authorization",
          description = "Bearer token in the format `Bearer <JWT>`",
          required = true,
          example = "Bearer eyJhbGciOiJIUzI1N.iIsInR5cCI6IkpXVCJ9..."
      ) @RequestHeader("Authorization") String authorizationHeader) {
    try {
      if (authorizationHeader == null || authorizationHeader.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Authorization header is missing");
      }
      adminService.sendPasswordResetEmail(emailRequest.getEmail(), authorizationHeader);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error : Invalid data");
    } catch (UnauthorizedException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: " + e.getMessage());
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
    } catch (UserNotVerifiedException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: " + e.getMessage() + ". Please activate your account via the email.");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: An unexpected error occurred");
    }
    return ResponseEntity.ok(true);
  }

}
