package org.ntnu.idatt2106.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.ntnu.idatt2106.backend.dto.user.PasswordResetRequest;
import org.ntnu.idatt2106.backend.dto.user.UserLoginRequest;
import org.ntnu.idatt2106.backend.dto.user.UserRegisterRequest;
import org.ntnu.idatt2106.backend.dto.user.UserTokenResponse;
import org.ntnu.idatt2106.backend.exceptions.AlreadyInUseException;
import org.ntnu.idatt2106.backend.exceptions.UserNotFoundException;
import org.ntnu.idatt2106.backend.security.JWT_token;
import org.ntnu.idatt2106.backend.service.LoginService;
import org.ntnu.idatt2106.backend.service.ReCaptchaService;
import org.ntnu.idatt2106.backend.service.ResetPasswordService;
import org.ntnu.idatt2106.backend.service.VerifyEmailService;
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
  private ResetPasswordService resetPasswordService;

  @Mock
  private VerifyEmailService verifyEmailService;
  
  @Mock
  private JWT_token jwtToken;

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
    assertEquals("Email already in use", response.getBody());
  }

  @Test
  @DisplayName("Test register method returns Badrequest with already used phone number")
  void testBadRequestWithUsedNumber() {
    UserRegisterRequest invalidUser = new UserRegisterRequest("email", "password123", "John", "Doe", "taken", "123456789");

    doThrow(new AlreadyInUseException("Invalid user data")).when(loginService).register(invalidUser);
    when(reCaptchaService.verifyToken(anyString())).thenReturn(true);
    ResponseEntity<String> response = userController.registerUser(invalidUser);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Email already in use", response.getBody());
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
    assertEquals("Invalid user data", response.getBody());
  }

  @Test
  @DisplayName("Test login method returns NOT_FOUND with non-existing user")
  void testLoginUserNotFound() {
    UserLoginRequest loginDTO = new UserLoginRequest("test@example.com", "wrongPassword");

    when(loginService.authenticate(loginDTO.getEmail(), loginDTO.getPassword()))
            .thenThrow(new UserNotFoundException("No user found with given email and password"));

    ResponseEntity<?> response = userController.login(loginDTO);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("No user found with given email and password", response.getBody());
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
    assertEquals("Invalid user data", response.getBody());
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
    assertEquals("User not found with given token", response.getBody());
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
    assertEquals("Invalid password", response.getBody());
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
    assertEquals("User not found with given token", response.getBody());
  }

  @Test
  @DisplayName("Should return 400 when IllegalArgumentException is thrown during email verification")
  void shouldReturnBadRequestOnInvalidEmailToken() {
    String token = "invalid";

    doThrow(new IllegalArgumentException("Invalid token")).when(verifyEmailService)
        .verifyEmail(token);

    ResponseEntity<String> response = userController.verifyEmail(token);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid token", response.getBody());
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
        .validateJwtToken(token);
    
    ResponseEntity<?> response = userController.isAuth("Bearer " + token);
    
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals(false, response.getBody());
  }
  
  @Test
  @DisplayName("Should return 400 Bad Request when an unexpected error occurs")
  void shouldReturnBadRequestOnUnexpectedError() {
    String token = "invalid-token";
    
    doThrow(new RuntimeException("Unexpected error")).when(jwtToken)
        .validateJwtToken(token);
    
    ResponseEntity<?> response = userController.isAuth("Bearer " + token);
    
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Unexpected error", response.getBody());
  }
}
