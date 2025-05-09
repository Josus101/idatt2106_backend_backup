package org.ntnu.idatt2106.backend.controller;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.ntnu.idatt2106.backend.dto.user.PasswordResetRequest;
import org.ntnu.idatt2106.backend.dto.user.UserAdminResponse;
import org.ntnu.idatt2106.backend.dto.user.UserLoginRequest;
import org.ntnu.idatt2106.backend.dto.user.UserPositionResponse;
import org.ntnu.idatt2106.backend.dto.user.UserPositionUpdate;
import org.ntnu.idatt2106.backend.dto.user.UserRegisterRequest;
import org.ntnu.idatt2106.backend.dto.user.UserTokenResponse;
import org.ntnu.idatt2106.backend.exceptions.AlreadyInUseException;
import org.ntnu.idatt2106.backend.exceptions.TokenExpiredException;
import org.ntnu.idatt2106.backend.exceptions.UnauthorizedException;
import org.ntnu.idatt2106.backend.exceptions.UserNotFoundException;
import org.ntnu.idatt2106.backend.model.Admin;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.repo.UserRepo;
import org.ntnu.idatt2106.backend.security.JWT_token;
import org.ntnu.idatt2106.backend.service.AdminService;
import org.ntnu.idatt2106.backend.service.HouseholdService;
import org.ntnu.idatt2106.backend.service.LoginService;
import org.ntnu.idatt2106.backend.service.ReCaptchaService;
import org.ntnu.idatt2106.backend.service.VerificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

  @InjectMocks
  private UserController userController;

  @Mock
  private LoginService loginService;

  @Mock
  ReCaptchaService reCaptchaService;

  @Mock
  private VerificationService resetPasswordService;

  @Mock
  private VerificationService verifyEmailService;

  @Mock
  private HouseholdService householdService;

  @Mock
  private JWT_token jwtToken;

  @Mock
  private UserRepo userRepo;

  @Mock
  private AdminService adminService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
  }

  @Test
  @DisplayName("Test register method returns success with valid user data")
  void testRegisterUserSuccess() {
    UserRegisterRequest validUser = new UserRegisterRequest("test@example.com", "password123", "John", "Doe", "12345678", "123456789");

    when(reCaptchaService.verifyToken(anyString())).thenReturn(true);

    ResponseEntity<String> response = userController.registerUser(validUser);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("User registered successfully", response.getBody());
  }

  @Test
  @DisplayName("Test register method returns BAD_REQUEST with invalid user data")
  void testRegisterUserBadRequest() {
    UserRegisterRequest invalidUser = new UserRegisterRequest("invalid-email", "password123", "John", "Doe", "123", "123456789");

    doThrow(new IllegalArgumentException("Invalid user data")).when(loginService).register(invalidUser);
    when(reCaptchaService.verifyToken(anyString())).thenReturn(true);

    ResponseEntity<String> response = userController.registerUser(invalidUser);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Invalid user data", response.getBody());
  }

  @Test
  @DisplayName("Test register method returns Bad request with already used email")
  void testBadrequestWithUsedEmail() {
    UserRegisterRequest invalidUser = new UserRegisterRequest("taken-email", "password123", "John", "Doe", "123", "123456789");

    doThrow(new AlreadyInUseException("Invalid user data")).when(loginService).register(invalidUser);

    when(reCaptchaService.verifyToken(anyString())).thenReturn(true);
    ResponseEntity<String> response = userController.registerUser(invalidUser);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Email already in use", response.getBody());
  }

  @Test
  @DisplayName("Test register method returns Badrequest with already used phone number")
  void testBadRequestWithUsedNumber() {
    UserRegisterRequest invalidUser = new UserRegisterRequest("email", "password123", "John", "Doe", "taken", "123456789");

    doThrow(new AlreadyInUseException("Invalid user data")).when(loginService).register(invalidUser);
    when(reCaptchaService.verifyToken(anyString())).thenReturn(true);
    ResponseEntity<String> response = userController.registerUser(invalidUser);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Email already in use", response.getBody());
  }

  @Test
  @DisplayName("Test login method returns token on successful login")
  void testLoginUserSuccess() {
    UserLoginRequest loginDTO = new UserLoginRequest("test@example.com", "password123");
    UserTokenResponse tokenDTO = new UserTokenResponse("validToken", System.currentTimeMillis());

    when(loginService.authenticate(loginDTO.getEmail(), loginDTO.getPassword())).thenReturn(tokenDTO);

    ResponseEntity<?> response = userController.login(loginDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(tokenDTO, response.getBody());
  }

  @Test
  @DisplayName("Test login method returns BAD_REQUEST with invalid credentials")
  void testLoginUserBadRequest() {
    UserLoginRequest loginDTO = new UserLoginRequest("test@example.com", "wrongPassword");

    when(loginService.authenticate(loginDTO.getEmail(), loginDTO.getPassword()))
        .thenThrow(new IllegalArgumentException("Invalid user data"));

    ResponseEntity<?> response = userController.login(loginDTO);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Invalid user data", response.getBody());
  }

  @Test
  @DisplayName("Test login method returns NOT_FOUND with non-existing user")
  void testLoginUserNotFound() {
    UserLoginRequest loginDTO = new UserLoginRequest("test@example.com", "wrongPassword");

    when(loginService.authenticate(loginDTO.getEmail(), loginDTO.getPassword()))
        .thenThrow(new UserNotFoundException("No user found with given email and password"));

    ResponseEntity<?> response = userController.login(loginDTO);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error: No user found with given email and password", response.getBody());
  }

  @Test
  @DisplayName("Test register method returns BAD_REQUEST with duplicate email")
  void testRegisterUserDuplicateEmail() {
    UserRegisterRequest userWithDuplicateEmail = new UserRegisterRequest("test@example.com", "password123", "John", "Doe", "12345678", "123456789");

    doThrow(new IllegalArgumentException("Email is already in use")).when(loginService).register(userWithDuplicateEmail);
    when(reCaptchaService.verifyToken(anyString())).thenReturn(true);

    ResponseEntity<String> response = userController.registerUser(userWithDuplicateEmail);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Email is already in use", response.getBody());
  }

  @Test
  @DisplayName("Test login method returns BAD_REQUEST with missing user data")
  void testLoginUserMissingData() {
    UserLoginRequest incompleteLoginDTO = new UserLoginRequest("", "");
    when(loginService.authenticate("", "")).thenThrow(new IllegalArgumentException("Invalid user data"));

    ResponseEntity<?> response = userController.login(incompleteLoginDTO);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Invalid user data", response.getBody());
  }

  @Test
  @DisplayName("Test register method returns BAD_REQUEST when password is empty")
  void testRegisterUserEmptyPassword() {
    UserRegisterRequest userWithEmptyPassword = new UserRegisterRequest("new@example.com", "", "Jane", "Doe", "87654321", "123456789");

    doThrow(new IllegalArgumentException("Invalid user data")).when(loginService).register(userWithEmptyPassword);
    when(reCaptchaService.verifyToken(anyString())).thenReturn(true);

    ResponseEntity<String> response = userController.registerUser(userWithEmptyPassword);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Invalid user data", response.getBody());
  }

  @Test
  @DisplayName("Test register method returns BAD_REQUEST when phone number is invalid")
  void testRegisterUserInvalidPhoneNumber() {
    UserRegisterRequest userWithInvalidPhoneNumber = new UserRegisterRequest("new@example.com", "securePassword", "Jane", "Doe", "123", "123456789");

    doThrow(new IllegalArgumentException("Invalid user data")).when(loginService).register(userWithInvalidPhoneNumber);
    when(reCaptchaService.verifyToken(anyString())).thenReturn(true);

    ResponseEntity<String> response = userController.registerUser(userWithInvalidPhoneNumber);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Invalid user data", response.getBody());
  }

  @Test
  @DisplayName("Should return 200 OK when password reset is successful")
  void shouldReturnOkOnSuccessfulPasswordReset() {
    String token = "valid-token";
    String newPassword = "NewPassword123";
    PasswordResetRequest password = new PasswordResetRequest(newPassword);


    ResponseEntity<String> response = userController.resetPassword(token, password);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Password reset successfully", response.getBody());
    verify(resetPasswordService).resetPassword(token, newPassword);
  }

  @Test
  @DisplayName("Should return 404 when UserNotFoundException is thrown during password reset")
  void shouldReturnNotFoundWhenUserNotFoundOnPasswordReset() {
    String token = "invalid-token";
    String newPassword = "NewPassword123";
    PasswordResetRequest password = new PasswordResetRequest(newPassword);

    doThrow(new UserNotFoundException("User not found")).when(resetPasswordService)
        .resetPassword(token, newPassword);

    ResponseEntity<String> response = userController.resetPassword(token, password);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error: User not found with given token", response.getBody());
  }

  @Test
  @DisplayName("Should return 400 when IllegalArgumentException is thrown during password reset")
  void shouldReturnBadRequestOnInvalidPassword() {
    String token = "valid-token";
    String newPassword = "";
    PasswordResetRequest password = new PasswordResetRequest(newPassword);

    doThrow(new IllegalArgumentException("Invalid password")).when(resetPasswordService)
        .resetPassword(token, newPassword);

    ResponseEntity<String> response = userController.resetPassword(token, password);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Invalid password", response.getBody());
  }

  @Test
  @DisplayName("Should return 200 OK when email is verified successfully")
  void shouldReturnOkOnSuccessfulEmailVerification() {
    String token = "valid-token";

    ResponseEntity<String> response = userController.verifyEmail(token);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Email verified successfully", response.getBody());
    verify(verifyEmailService).verifyEmail(token);
  }

  @Test
  @DisplayName("Should return 404 when UserNotFoundException is thrown during email verification")
  void shouldReturnNotFoundOnEmailVerificationUserNotFound() {
    String token = "invalid-token";

    doThrow(new UserNotFoundException("User not found")).when(verifyEmailService)
        .verifyEmail(token);

    ResponseEntity<String> response = userController.verifyEmail(token);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error: User not found with given token", response.getBody());
  }

  @Test
  @DisplayName("Should return 400 when IllegalArgumentException is thrown during email verification")
  void shouldReturnBadRequestOnInvalidEmailToken() {
    String token = "invalid";

    doThrow(new IllegalArgumentException("Invalid token")).when(verifyEmailService)
        .verifyEmail(token);

    ResponseEntity<String> response = userController.verifyEmail(token);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Invalid token", response.getBody());
  }

  @Test
  @DisplayName("Should return true when token is valid")
  void shouldReturnTrueWhenTokenIsValid() {
    String token = "valid-token";

    ResponseEntity<?> response = userController.isAuth(token);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(true, response.getBody());
  }

  @Test
  @DisplayName("Should return false when token is invalid")
  void shouldReturnFalseWhenTokenIsInvalid() {
    String token = "invalid-token";

    doThrow(new IllegalArgumentException("Token is invalid")).when(jwtToken)
        .validateJwtToken(token, false);

    ResponseEntity<?> response = userController.isAuth("Bearer " + token);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals(false, response.getBody());
  }

  @Test
  @DisplayName("Should return 400 Bad Request when an unexpected error occurs")
  void shouldReturnBadRequestOnUnexpectedError() {
    String token = "invalid-token";

    doThrow(new RuntimeException("Unexpected error")).when(jwtToken)
        .validateJwtToken(token, false);

    ResponseEntity<?> response = userController.isAuth("Bearer " + token);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Unexpected error", response.getBody());
  }

  @Test
  @DisplayName("Should return bad request when TokenExpired is thrown during email verification")
  void shouldReturnBadRequestOnExpiredEmailToken() {
    String token = "expired";

    doThrow(new TokenExpiredException("lmao your token gone man")).when(verifyEmailService)
        .verifyEmail(token);

    ResponseEntity<String> response = userController.verifyEmail(token);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Token expired", response.getBody());
  }

  @Test
  @DisplayName("Should return IllegalArgumentException when RuntimeException is thrown during email verification")
  void shouldReturnInternalServerErrorWhenRuntimeThrown() {
    String token = "expired";

    doThrow(new RuntimeException("lmao your token gone man")).when(verifyEmailService)
        .verifyEmail(token);

    ResponseEntity<String> response = userController.verifyEmail(token);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Error: An error occurred during email verification", response.getBody());
  }
  @Test
  @DisplayName("Should return 200 OK when location update is successful")
  void shouldReturnOkOnSuccessfulLocationUpdate() {
    // Arrange
    UserPositionUpdate positionUpdate = new UserPositionUpdate(59.91, 10.75); // example lat/lng
    String token = "Bearer valid.jwt.token";
    User user = new User();

    // Mock behavior
    when(jwtToken.getUserByToken("valid.jwt.token")).thenReturn(user);
    when(userRepo.save(user)).thenReturn(user);

    // Act
    ResponseEntity<String> response = userController.updateLocation(positionUpdate, token);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Location updated successfully", response.getBody());

    // Verify interaction
    verify(userRepo).save(user);
    assertEquals(positionUpdate.getLatitude(), user.getLatitude());
    assertEquals(positionUpdate.getLongitude(), user.getLongitude());
  }


  @Test
  @DisplayName("Should return 401 when token is invalid")
  void shouldReturnUnauthorizedWithInvalidToken() {
    UserPositionUpdate positionUpdate = new UserPositionUpdate(59.91, 10.75);
    String token = "Bearer invalid.jwt.token";

    when(jwtToken.getUserByToken("invalid.jwt.token")).thenReturn(null);

    ResponseEntity<String> response = userController.updateLocation(positionUpdate, token);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Error: Unauthorized - Invalid token", response.getBody());
  }

  @Test
  @DisplayName("Should return 400 on IllegalArgumentException")
  void shouldReturnBadRequestOnIllegalArgument() {
    UserPositionUpdate positionUpdate = new UserPositionUpdate(0, 0);

    String token = "Bearer bad.jwt.token";

    when(jwtToken.getUserByToken("bad.jwt.token")).thenThrow(new IllegalArgumentException());

    ResponseEntity<String> response = userController.updateLocation(positionUpdate, token);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Invalid location data", response.getBody());
  }

  @Test
  @DisplayName("Should return 500 on unexpected exception during updateLocation")
  void shouldReturnInternalServerErrorOnUnexpectedException() {
    UserPositionUpdate positionUpdate = new UserPositionUpdate(59.91, 10.75);
    String token = "Bearer exception.jwt.token";

    when(jwtToken.getUserByToken("exception.jwt.token")).thenThrow(new RuntimeException());

    ResponseEntity<String> response = userController.updateLocation(positionUpdate, token);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Error: An unexpected error occurred during location update", response.getBody());
  }

  @Test
  @DisplayName("Should return 200 OK when household location is retrieved")
  void shouldReturnOkWhenHouseholdLocationRetrieved() {
    String token = "Bearer valid.jwt.token";
    User user = new User();
    int householdId = 1;

    when(jwtToken.getUserByToken("valid.jwt.token")).thenReturn(user);
    when(householdService.getUserPositions(user)).thenReturn(List.of(new UserPositionResponse(59.91, 10.75, "2023-10-01T12:00:00Z", 1, "John Doe")));
    ResponseEntity<?> response = userController.getPositionsFromHousehold(householdId, token);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  @DisplayName("Should return 401 when token is invalid for household location")
  void shouldReturnUnauthorizedOnInvalidTokenForHousehold() {
    String token = "Bearer invalid.jwt.token";

    when(jwtToken.getUserByToken("invalid.jwt.token")).thenReturn(null);

    ResponseEntity<?> response = userController.getPositionsFromHousehold(1, token);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Error: Unauthorized - Invalid token", response.getBody());
  }

  @Test
  @DisplayName("Should return 400 on IllegalArgumentException for household location")
  void shouldReturnBadRequestForIllegalArgumentInHousehold() {
    String token = "Bearer invalid.jwt.token";

    when(jwtToken.getUserByToken("invalid.jwt.token")).thenThrow(new IllegalArgumentException());

    ResponseEntity<?> response = userController.getPositionsFromHousehold(1, token);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Invalid household ID", response.getBody());
  }

  @Test
  @DisplayName("Should return 500 on unexpected error for household location")
  void shouldReturnInternalServerErrorForHouseholdLocation() {
    String token = "Bearer error.jwt.token";

    when(jwtToken.getUserByToken("error.jwt.token")).thenThrow(new RuntimeException());

    ResponseEntity<?> response = userController.getPositionsFromHousehold(1, token);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Error: An unexpected error occurred during location retrieval", response.getBody());
  }

  @Test
  @DisplayName("Should return 200 OK when user has access to locations")
  void shouldReturnOkWhenUserHasAccessToLocations() {
    String token = "Bearer valid.jwt.token";
    User user = new User();

    when(jwtToken.getUserByToken("valid.jwt.token")).thenReturn(user);
    when(householdService.getUserPosition(user)).thenReturn(new UserPositionResponse(59.91, 10.75, "2023-10-01T12:00:00Z", 1, "John Doe"));

    ResponseEntity<?> response = userController.getUserPosition(token);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
  }

  @Test
  @DisplayName("Should return 401 when token is invalid for general household location")
  void shouldReturnUnauthorizedForGeneralHouseholdInvalidToken() {
    String token = "Bearer invalid.jwt.token";

    when(jwtToken.getUserByToken("invalid.jwt.token")).thenReturn(null);

    ResponseEntity<?> response = userController.getUserPosition(token);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Error: Unauthorized - Invalid token", response.getBody());
  }

  @Test
  @DisplayName("Should return 400 on bad household ID exception")
  void shouldReturnBadRequestForGeneralHouseholdIllegalArgument() {
    String token = "Bearer bad.jwt.token";

    when(jwtToken.getUserByToken("bad.jwt.token")).thenThrow(new IllegalArgumentException());

    ResponseEntity<?> response = userController.getUserPosition(token);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Invalid household ID", response.getBody());
  }

  @Test
  @DisplayName("Should return 500 on unexpected error in general household location")
  void shouldReturnInternalServerErrorForGeneralHouseholdError() {
    String token = "Bearer error.jwt.token";

    when(jwtToken.getUserByToken("error.jwt.token")).thenThrow(new RuntimeException());

    ResponseEntity<?> response = userController.getUserPosition(token);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Error: An unexpected error occurred during location retrieval", response.getBody());
  }

  @Test
  @DisplayName("Should return 200 and all users when authorized admin token is provided")
  void shouldReturnAllUsersWhenAuthorizedAdminTokenIsProvided() {
    String token = "valid.jwt.token";
    Admin mockAdmin = new Admin();
    List<UserAdminResponse> mockUsers = List.of(new UserAdminResponse(), new UserAdminResponse());

    when(jwtToken.getAdminUserByToken(token)).thenReturn(mockAdmin);
    when(adminService.getAllUsers(any())).thenReturn(mockUsers);

    ResponseEntity<?> response = userController.getAllUsers("Bearer " + token);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(mockUsers, response.getBody());
  }

  @Test
  @DisplayName("Should return 401 when no valid admin token is provided")
  void shouldReturnUnauthorizedWhenNoValidAdminToken() {
    String token = "invalid.jwt.token";

    when(jwtToken.getAdminUserByToken(token)).thenReturn(null);
    doThrow(new UnauthorizedException("User not found"))
        .when(adminService).getAllUsers(any());
    ResponseEntity<?> response = userController.getAllUsers("Bearer " + token);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Error: Unauthorized - Invalid token", response.getBody());
  }

  @Test
  @DisplayName("Should return 500 when exception occurs in getAllUsers")
  void shouldReturnInternalServerErrorWhenExceptionOccursInGetAllUsers() {
    String token = "valid.jwt.token";

    doThrow(new RuntimeException("Unexpected error")).when(adminService)
        .getAllUsers(any());
    when(jwtToken.getAdminUserByToken(token)).thenThrow(new RuntimeException("Unexpected"));

    ResponseEntity<?> response = userController.getAllUsers("Bearer " + token);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Error: An error occurred while retrieving all users", response.getBody());
  }

  @Test
  @DisplayName("Should delete user and return 200 when admin is authorized and user exists")
  void shouldDeleteUserSuccessfullyWhenAuthorized() {
    String token = "valid.jwt.token";
    int userId = 1;
    Admin mockAdmin = new Admin();
    User mockUser = new User();

    when(jwtToken.getAdminUserByToken(token)).thenReturn(mockAdmin);
    when(userRepo.findById(userId)).thenReturn(java.util.Optional.of(mockUser));

    ResponseEntity<?> response = userController.deleteUser(userId, "Bearer " + token);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("User deleted successfully", response.getBody());
  }

  @Test
  @DisplayName("Should return 404 when user is not found for deletion")
  void shouldReturnNotFoundWhenUserNotFound() {
    String token = "valid.jwt.token";
    int userId = 1;
    Admin mockAdmin = new Admin();

    when(jwtToken.getAdminUserByToken(token)).thenReturn(mockAdmin);
    doThrow(new UserNotFoundException("User not found"))
        .when(adminService).deleteUser(any(), any());
    ResponseEntity<?> response = userController.deleteUser(userId, "Bearer " + token);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error: User not found", response.getBody());
  }

  @Test
  @DisplayName("Should return 401 when token is not from an admin")
  void shouldReturnUnauthorizedWhenTokenNotAdmin() {
    String token = "invalid.jwt.token";

    when(jwtToken.getAdminUserByToken(token)).thenReturn(null);
    doThrow(new UnauthorizedException("User not found"))
        .when(adminService).deleteUser(any(), any());
    ResponseEntity<?> response = userController.deleteUser(1, "Bearer " + token);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Error: Unauthorized - Invalid token", response.getBody());
  }

  @Test
  @DisplayName("Should return 500 when an unexpected error occurs during deletion")
  void shouldReturnInternalServerErrorWhenExceptionThrownDuringDelete() {
    String token = "valid.jwt.token";
    int userId = 1;
    doThrow(new RuntimeException("Unexpected error")).when(adminService).deleteUser(any(), any());
    ResponseEntity<?> response = userController.deleteUser(userId, "Bearer " + token);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Error: An error occurred while deleting the user", response.getBody());
  }



}
