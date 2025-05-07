package org.ntnu.idatt2106.backend.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.ntnu.idatt2106.backend.dto.user.*;
import org.ntnu.idatt2106.backend.exceptions.AlreadyInUseException;
import org.ntnu.idatt2106.backend.exceptions.MailSendingFailedException;
import org.ntnu.idatt2106.backend.exceptions.TokenExpiredException;
import org.ntnu.idatt2106.backend.exceptions.UserNotFoundException;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.repo.UserRepo;
import org.ntnu.idatt2106.backend.security.JWT_token;
import org.ntnu.idatt2106.backend.service.*;
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
  ReCaptchaService reCaptchaService;

  @Autowired
  private ResetPasswordService resetPasswordService;

  @Autowired
  private VerifyEmailService verifyEmailService;

  @Autowired
  private HouseholdService householdService;

  @Autowired
  private UserRepo userRepo;
  
  @Autowired
  private JWT_token jwtToken;

  @Autowired
  UserSettingsService userSettingsService;

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
              mediaType = "application/json",
              schema = @Schema(example = "User registered successfully"))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid user data",
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "InvalidUserData",
                      summary = "Invalid user data",
                      value = "Error: Invalid user data"
                  ),
                  @ExampleObject(
                      name = "EmailAlreadyInUse",
                      summary = "Email already in use",
                      value = "Error: Email already in use"
                  )
              }
          )
      ),
//      @ApiResponse(
//          responseCode = "418",
//          description = "Invalid Captcha token",
//          content = @Content(
//              mediaType = "application/json",
//              schema = @Schema(example = "Invalid Captcha token"))
//      )
  })
  public ResponseEntity<String> registerUser(
    @RequestBody UserRegisterRequest userRegister) {
    try {
      if (!reCaptchaService.verifyToken(userRegister.getReCaptchaToken()))  {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid Captcha token");
      }

          loginService.register(userRegister);
          return ResponseEntity.ok("User registered successfully");
      }
      catch (IllegalArgumentException e) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
      } catch (AlreadyInUseException e) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Email already in use");
      } catch (MailSendingFailedException e) {
          System.out.println("Failed to send verification email. Either mail is invalid, or you're "
              + "missing .env file " + e.getMessage());
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Failed to send verification email");
      } catch (IllegalStateException e) {
          System.out.println("You sure you have the .env file? " + e.getMessage());
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Failed to send verification email");
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: An error occurred during registration");
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
              mediaType = "application/json",
              schema = @Schema(implementation = UserTokenResponse.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "No user found with given email and password",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Invalid user data"))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid user data",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: No user found with given email and password"))
      )
  })
  public ResponseEntity<?> login(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "User login request containing email and password",
          required = true,
          content = @Content(
              schema = @Schema(implementation = UserLoginRequest.class)
          )
      ) @RequestBody UserLoginRequest userLogin
  ){
    UserTokenResponse token;
    try {
      token = loginService.authenticate(userLogin.getEmail(), userLogin.getPassword());
    }
    catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid user data");
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: No user found with given email and password");
    }
    return ResponseEntity.ok(token);
  }
  
  /**
   * Endpoint to verify if the provided JWT token is valid.
   *
   * This endpoint checks the validity of the JWT token. If the token is valid, it returns `true` with
   * a 200 OK status. If the token is invalid, it returns
   * `false` with a 401 Unauthorized status. If an error occurs, returns BadRequest with the error message.
   *
   * @param authorizationHeader The "Authorization" header containing the JWT token
   *                            in the format "Bearer <JWT>".
   * @return A ResponseEntity containing a boolean value (`true` or `false`)
   *         indicating the validity of the token.
   */
  @PostMapping("/is-auth")
  @Operation(
      summary = "Is Authenticated",
      description = "Validates the provided JWT token and returns a boolean value indicating its validity."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Valid Token - `true` returned",
          content = @Content(
              schema = @Schema(implementation = Boolean.class)
          )
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Invalid Token - `false` returned",
          content = @Content(
              schema = @Schema(example = "false")
          )
      ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request - Error message returned",
            content = @Content(
                schema = @Schema(example = "Error: Error occurred while validating token")
            )
        )
  })
  public ResponseEntity<?> isAuth(
      @Parameter(
          name = "Authorization",
          description = "Bearer token in the format `Bearer <JWT>`",
          required = true,
          example = "Bearer eyJhbGciOiJIUzI1N.iIsInR5cCI6IkpXVCJ9..."
      )
      @RequestHeader("Authorization") String authorizationHeader)
  {
    try {
      String token = authorizationHeader.substring(7);
      jwtToken.validateJwtToken(token, false);
      return ResponseEntity.ok(true);
    } catch (IllegalArgumentException | TokenExpiredException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: "+ e.getMessage());
    }
  }

  /**
   * Endpoint for resetting the password of a user.
   * @param token the token for password reset
   * @param password the new password for the user
   * @return a response entity indicating the result of the operation
   */
  @PutMapping("/reset-password/{token}")
  @Operation(
      summary = "Reset password",
      description = "Resets the password for the user with the given token"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Password reset successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Password reset successfully"))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid password or token",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: rawPassword cannot be null"))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "User not found with given token",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: User not found with given token"))
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: An unexpected error occurred during password reset"))
      )
  })
  public ResponseEntity<String> resetPassword(
      @Parameter(description = "Token for password reset", example = "1234567890abcdef")
      @PathVariable String token,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "New password for the user",
          required = true,
          content = @Content(
              schema = @Schema(implementation = PasswordResetRequest.class)
          )
      ) @RequestBody PasswordResetRequest password
  ){
    try {
      resetPasswordService.resetPassword(token, password.getPassword());
      return ResponseEntity.ok("Password reset successfully");
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Error: User not found with given token");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Error: "+e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error: An unexpected error occurred during password reset");
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
          description = "Email verified successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Email verified successfully"))),
      @ApiResponse(
          responseCode = "404",
          description = "User not found with given token",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: User not found with given token"))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid token",
          content = @Content(
              mediaType = "application/json",
              examples = {
                  @ExampleObject(
                      name = "InvalidToken",
                      summary = "Token not valid",
                      value = "Error: Invalid token"
                  ),
                  @ExampleObject(
                      name = "ExpiredToken",
                      summary = "Token expired",
                      value = "Error: Token expired"
                  )
              }
          )
      )
  })
  public ResponseEntity<String> verifyEmail(
      @Parameter(
              description = "Token for email verification",
              example = "1234567890abcdef"
      ) @PathVariable String token
  ){
    try {
      verifyEmailService.verifyEmail(token);
      System.out.println("Email verified successfully");
      return ResponseEntity.ok("Email verified successfully");
    } catch (UserNotFoundException e) {
      System.out.println("User not found with given token");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: User not found with given token");
    } catch (IllegalArgumentException e) {
      System.out.println("Invalid token");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid token");
    } catch (TokenExpiredException e) {
      System.out.println("Token expired");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Token expired");
    } catch (Exception e) {
      System.out.println("An error occurred during email verification: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: An error occurred during email verification");
    }
  }

  /**
   * Endpoint for updating last known location of a user.
   * @param positionUpdate the new position of the user
   * @param authorizationHeader the JWT token for authorization
   * @return a response entity indicating the result of the operation
   */
  @PutMapping("/update-location")
  @Operation(
      summary = "Update user location",
      description = "Updates the last known location of the user"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Location updated successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Location updated successfully"))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid location data",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Invalid location data"))
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized - Invalid token",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Unauthorized - Invalid token"))
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: An unexpected error occurred during location update"))
      )
  })
  public ResponseEntity<String> updateLocation(
      @Parameter(description = "Position of the user", required = true,
          schema = @Schema(implementation = UserPositionUpdate.class))
      @RequestBody UserPositionUpdate positionUpdate,
      @Parameter(description = "Authorization header with JWT token", example = "Bearer <token>")
      @RequestHeader("Authorization") String authorizationHeader
  ) {
    try {
      String token = authorizationHeader.substring(7);
      User user = jwtToken.getUserByToken(token);
      if (user != null) {
        user.setPosition(positionUpdate);
        userRepo.save(user);
        return ResponseEntity.ok("Location updated successfully");
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Unauthorized - Invalid token");
      }
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid location data");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: An unexpected error occurred during location update");
    }
  }

  /**
   * Endpoint for getting the last known location of all users in a household.
   * @param authorizationHeader the JWT token for authorization
   * @param householdId the ID of the household
   * @return a response entity containing the user's last known location
   */
  @GetMapping("/locations/{householdId}")
  @Operation(
      summary = "Get user location",
      description = "Gets the last known location of all users in a household"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Location retrieved successfully",
          content = @Content(
              mediaType = "application/json",
              array = @ArraySchema(
                  schema = @Schema(implementation = UserPositionResponse.class))
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Error: Invalid household ID",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Invalid household ID"))
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Error: Unauthorized - Invalid token",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Unauthorized - Invalid token"))
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Error: Internal server error",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "An unexpected error occurred during location retrieval"))
      )
  })
  public ResponseEntity<?> getPositionsFromHousehold(
      @Parameter(description = "Id of the household", example = "1")
      @PathVariable int householdId,
      @Parameter(description = "Authorization header with JWT token", example = "Bearer <token>")
      @RequestHeader("Authorization") String authorizationHeader) {
    try {
      String token = authorizationHeader.substring(7);
      User user = jwtToken.getUserByToken(token);
      if (user != null) {
        return ResponseEntity.ok(householdService.getUserPositions(householdId, user));
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Unauthorized - Invalid token");
      }
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid household ID");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: An unexpected error occurred during location retrieval");
    }
  }


  /**
   * Endpoint for getting the last known location of all users in all household the requesting user
   * is a member of.
   * @param authorizationHeader the JWT token for authorization
   * @return a response entity containing the user's last known location
   */
  @GetMapping("/locations")
  @Operation(
      summary = "Get user location",
      description = "Gets the last known location of all users the requesting user has access to"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Location retrieved successfully",
          content = @Content(
              mediaType = "application/json",
              array = @ArraySchema(
                  schema = @Schema(implementation = UserPositionResponse.class))
              )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Error: Invalid household ID",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Invalid household ID"))
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Error: Unauthorized - Invalid token",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Unauthorized - Invalid token"))
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Error: Internal server error",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "An unexpected error occurred during location retrieval"))
      )
  })
  public ResponseEntity<?> getPositionsFromHousehold(
      @Parameter(description = "Authorization header with JWT token", example = "Bearer <token>")
      @RequestHeader("Authorization") String authorizationHeader) {
    try {
      String token = authorizationHeader.substring(7);
      User user = jwtToken.getUserByToken(token);
      if (user != null) {
        return ResponseEntity.ok(householdService.getUserPositions(user));
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Unauthorized - Invalid token");
      }
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid household ID");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: An unexpected error occurred during location retrieval");
    }
  }

  /**
   * Endpoint for saving user settings.
   * @param settings the user settings to be saved
   * @param authorizationHeader the JWT token for authorization
   * @return a response entity indicating the result of the operation
   */
  @PostMapping("/settings/save")
  @Operation(
      summary = "Save user settings",
      description = "Saves the user settings for the authenticated user"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "User settings saved successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "User settings saved successfully"))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid user settings data",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Invalid user settings data"))
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized - Invalid token",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Unauthorized - Invalid token"))
      )
  })
  public ResponseEntity<?> saveUserSettings(
          @RequestBody UserStoreSettingsRequest settings,
          @RequestHeader("Authorization") String authorizationHeader
  ){
    try {
      if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        String token = authorizationHeader.substring(7);
        User user = jwtToken.getUserByToken(token);
        if (user != null) {
          userSettingsService.saveUserSettings(user.getId(), settings);
          return ResponseEntity.ok("User settings saved successfully");
        }
      }
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Unauthorized");
    } catch(Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Invalid user settings data");
    }

  }

  /**
   * Endpoint for getting user settings.
   * @param authorizationHeader the JWT token for authorization
   * @return a response entity containing the user's settings
   */
  @GetMapping("/settings/get")
  @Operation(
      summary = "Get user settings",
      description = "Gets the user settings for the authenticated user"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "User settings retrieved successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = UserStoreSettingsRequest.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "User settings not found",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: User settings not found"))
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized - Invalid token",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Unauthorized - Invalid token"))
      )
  })
  public ResponseEntity<?> getUserSettings(
          @RequestHeader("Authorization") String authorizationHeader
  ){
    try {
      if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        String token = authorizationHeader.substring(7);
        User user = jwtToken.getUserByToken(token);
        if (user != null) {
          UserStoreSettingsRequest settings = userSettingsService.getUserSettings(user.getId());
          if (settings != null) {
            return ResponseEntity.status(HttpStatus.OK).body(settings);
          } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: User settings not found");
          }
        }
      }
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Unauthorized");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: An error occurred while retrieving user settings");
    }
  }


}

