package org.ntnu.idatt2106.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.ntnu.idatt2106.backend.dto.UserLoginDTO;
import org.ntnu.idatt2106.backend.dto.UserRegisterDTO;
import org.ntnu.idatt2106.backend.dto.UserTokenDTO;
import org.ntnu.idatt2106.backend.service.LoginService;
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

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
  }

  @Test
  @DisplayName("Test register method returns success with valid user data")
  void testRegisterUserSuccess() {
    UserRegisterDTO validUser = new UserRegisterDTO("test@example.com", "password123", "John", "Doe", "12345678");

    ResponseEntity<String> response = userController.registerUser(validUser);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("User registered successfully", response.getBody());
  }

  @Test
  @DisplayName("Test register method returns BAD_REQUEST with invalid user data")
  void testRegisterUserBadRequest() {
    UserRegisterDTO invalidUser = new UserRegisterDTO("invalid-email", "password123", "John", "Doe", "123");

    doThrow(new IllegalArgumentException("Invalid user data")).when(loginService).register(invalidUser);

    ResponseEntity<String> response = userController.registerUser(invalidUser);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid user data", response.getBody());
  }

  @Test
  @DisplayName("Test login method returns token on successful login")
  void testLoginUserSuccess() {
    UserLoginDTO loginDTO = new UserLoginDTO("test@example.com", "password123");
    UserTokenDTO tokenDTO = new UserTokenDTO("validToken", System.currentTimeMillis());

    when(loginService.authenticate(loginDTO.getEmail(), loginDTO.getPassword())).thenReturn(tokenDTO);

    ResponseEntity<?> response = userController.login(loginDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(tokenDTO, response.getBody());
  }

  @Test
  @DisplayName("Test login method returns BAD_REQUEST with invalid credentials")
  void testLoginUserBadRequest() {
    UserLoginDTO loginDTO = new UserLoginDTO("test@example.com", "wrongPassword");

    when(loginService.authenticate(loginDTO.getEmail(), loginDTO.getPassword()))
        .thenThrow(new IllegalArgumentException("Invalid user data"));

    ResponseEntity<?> response = userController.login(loginDTO);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid user data", response.getBody());
  }

  @Test
  @DisplayName("Test register method returns BAD_REQUEST with duplicate email")
  void testRegisterUserDuplicateEmail() {
    UserRegisterDTO userWithDuplicateEmail = new UserRegisterDTO("test@example.com", "password123", "John", "Doe", "12345678");

    doThrow(new IllegalArgumentException("Email is already in use")).when(loginService).register(userWithDuplicateEmail);

    ResponseEntity<String> response = userController.registerUser(userWithDuplicateEmail);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid user data", response.getBody());
  }

  @Test
  @DisplayName("Test login method returns BAD_REQUEST with missing user data")
  void testLoginUserMissingData() {
    UserLoginDTO incompleteLoginDTO = new UserLoginDTO("", "");
    when(loginService.authenticate("", "")).thenThrow(new IllegalArgumentException("Invalid user data"));

    ResponseEntity<?> response = userController.login(incompleteLoginDTO);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid user data", response.getBody());
  }

  @Test
  @DisplayName("Test register method returns BAD_REQUEST when password is empty")
  void testRegisterUserEmptyPassword() {
    UserRegisterDTO userWithEmptyPassword = new UserRegisterDTO("new@example.com", "", "Jane", "Doe", "87654321");

    doThrow(new IllegalArgumentException("Invalid user data")).when(loginService).register(userWithEmptyPassword);

    ResponseEntity<String> response = userController.registerUser(userWithEmptyPassword);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid user data", response.getBody());
  }

  @Test
  @DisplayName("Test register method returns BAD_REQUEST when phone number is invalid")
  void testRegisterUserInvalidPhoneNumber() {
    UserRegisterDTO userWithInvalidPhoneNumber = new UserRegisterDTO("new@example.com", "securePassword", "Jane", "Doe", "123");

    doThrow(new IllegalArgumentException("Invalid user data")).when(loginService).register(userWithInvalidPhoneNumber);
    ResponseEntity<String> response = userController.registerUser(userWithInvalidPhoneNumber);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid user data", response.getBody());
  }
}
