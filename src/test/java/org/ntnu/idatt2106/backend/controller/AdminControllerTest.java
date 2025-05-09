package org.ntnu.idatt2106.backend.controller;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.ntnu.idatt2106.backend.dto.admin.AdminGetResponse;
import org.ntnu.idatt2106.backend.dto.admin.AdminLoginRequest;
import org.ntnu.idatt2106.backend.dto.admin.AdminRegisterRequest;
import org.ntnu.idatt2106.backend.dto.user.EmailRequest;
import org.ntnu.idatt2106.backend.exceptions.UnauthorizedException;
import org.ntnu.idatt2106.backend.exceptions.UserNotFoundException;
import org.ntnu.idatt2106.backend.exceptions.UserNotVerifiedException;
import org.ntnu.idatt2106.backend.service.AdminService;
import org.ntnu.idatt2106.backend.service.VerificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminControllerTest {

  @InjectMocks
  private AdminController adminController;

  @Mock
  private AdminService adminService;
  
  @Mock
  private VerificationService verificationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Should return 200 when admin is successfully created")
  void testAddAdminUserSuccess() {
    AdminRegisterRequest dto = new AdminRegisterRequest("admin", "test@mail.com");

    ResponseEntity<Boolean> response = adminController.addAdminUser(dto, "Bearer token");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody());
    verify(adminService, times(1)).register("admin", "test@mail.com", "Bearer token");
  }

  @Test
  @DisplayName("Should return 401 if unauthorized during admin creation")
  void testAddAdminUserUnauthorized() {
    AdminRegisterRequest dto = new AdminRegisterRequest("admin", "test@mail.com");
    doThrow(new UnauthorizedException("Not allowed")).when(adminService).register(anyString(), anyString(), anyString());

    ResponseEntity<Boolean> response = adminController.addAdminUser(dto, "Bearer token");

    assertFalse(response.getBody());
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  @DisplayName("Should return 500 on exception during admin creation")
  void testAddAdminUserException() {
    AdminRegisterRequest dto = new AdminRegisterRequest("admin","test@mail.com");
    doThrow(new RuntimeException("Unexpected")).when(adminService).register(any(), any(), any());

    ResponseEntity<Boolean> response = adminController.addAdminUser(dto, "Bearer token");

    assertFalse(response.getBody());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  @DisplayName("Should return 200 when admin is elevated successfully")
  void testElevateAdminSuccess() {
    ResponseEntity<Boolean> response = adminController.elevateAdminUser("123", "Bearer token");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody());
    verify(adminService, times(1)).elevateAdmin("123", "Bearer token");
  }

  @Test
  @DisplayName("Should return 400 if invalid ID during elevation")
  void testElevateAdminBadRequest() {
    doThrow(new IllegalArgumentException()).when(adminService).elevateAdmin(anyString(), anyString());

    ResponseEntity<Boolean> response = adminController.elevateAdminUser("invalid", "Bearer token");

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertFalse(response.getBody());
  }

  @Test
  @DisplayName("Should return 401 if unauthorized during elevation")
  void testElevateAdminUnauthorized() {
    doThrow(new UnauthorizedException("No access")).when(adminService).elevateAdmin(anyString(), anyString());

    ResponseEntity<Boolean> response = adminController.elevateAdminUser("123", "Bearer token");

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertFalse(response.getBody());
  }

  @Test
  @DisplayName("Should return 500 on exception during elevation")
  void testElevateAdminException() {
    doThrow(new RuntimeException("Unexpected")).when(adminService).elevateAdmin(anyString(), anyString());

    ResponseEntity<Boolean> response = adminController.elevateAdminUser("123", "Bearer token");

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertFalse(response.getBody());
  }

  @Test
  @DisplayName("Should return token on successful admin login")
  void testAdminLoginSuccess() {
    AdminLoginRequest loginDTO = new AdminLoginRequest("admin", "securePass", "token");
    when(adminService.authenticate("admin", "securePass", "token")).thenReturn("validToken");

    ResponseEntity<?> response = adminController.login(loginDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("validToken", response.getBody());
  }

  @Test
  @DisplayName("Should return 400 on invalid admin login")
  void testAdminLoginBadRequest() {
    AdminLoginRequest loginDTO = new AdminLoginRequest("admin","wrongPass", "token");
    when(adminService.authenticate(any(), any(), any()))
        .thenThrow(new IllegalArgumentException("Invalid credentials"));

    ResponseEntity<?> response = adminController.login(loginDTO);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Invalid credentials", response.getBody());
  }

  @Test
  @DisplayName("Should return 500 on exception during login")
  void testAdminLoginException() {
    AdminLoginRequest loginDTO = new AdminLoginRequest("admin","pass", "token");
    when(adminService.authenticate(any(), any(), any()))
        .thenThrow(new RuntimeException("Unexpected"));

    ResponseEntity<?> response = adminController.login(loginDTO);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Error: An unexpected error occurred", response.getBody());
  }

  @Test
  @DisplayName("Should return 200 when admin is deleted successfully")
  void testDeleteAdminSuccess() {
    ResponseEntity<?> response = adminController.deleteAdminUser("123", "Bearer token");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(true, response.getBody());
    verify(adminService, times(1)).exterminateAdmin("123", "Bearer token");
  }

  @Test
  @DisplayName("Should return 400 on bad request during delete")
  void testDeleteAdminBadRequest() {
    doThrow(new IllegalArgumentException()).when(adminService).exterminateAdmin(anyString(), anyString());

    ResponseEntity<?> response = adminController.deleteAdminUser("invalid", "Bearer token");

    assertEquals(false, response.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @DisplayName("Should return 401 on unauthorized delete attempt")
  void testDeleteAdminUnauthorized() {
    doThrow(new UnauthorizedException("Not allowed")).when(adminService).exterminateAdmin(anyString(), anyString());

    ResponseEntity<?> response = adminController.deleteAdminUser("123", "Bearer token");

    assertEquals(false, response.getBody());
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  @DisplayName("Should return 500 on internal error during delete")
  void testDeleteAdminException() {
    doThrow(new RuntimeException("Unexpected")).when(adminService).exterminateAdmin(anyString(), anyString());

    ResponseEntity<?> response = adminController.deleteAdminUser("123", "Bearer token");

    assertEquals(false, response.getBody());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  @DisplayName("Should return 200 with admin list on success")
  void testGetAllAdminsSuccess() {
    List<AdminGetResponse> mockAdmins = List.of(
        new AdminGetResponse(1, "admin1", "admin1", true),
        new AdminGetResponse(2, "admin2", "admin1", false)
    );
    when(adminService.getAllAdmins("Bearer token")).thenReturn(mockAdmins);

    ResponseEntity<?> response = adminController.getAllAdmins("Bearer token");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(mockAdmins, response.getBody());
    verify(adminService, times(1)).getAllAdmins("Bearer token");
  }

  @Test
  @DisplayName("Should return 401 when unauthorized")
  void testGetAllAdminsUnauthorized() {
    doThrow(new UnauthorizedException("No access")).when(adminService).getAllAdmins("Bearer token");

    ResponseEntity<?> response = adminController.getAllAdmins("Bearer token");

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertFalse((Boolean) response.getBody());
  }

  @Test
  @DisplayName("Should return 404 when no admins found")
  void testGetAllAdminsNotFound() {
    doThrow(new UserNotFoundException("No admins found")).when(adminService).getAllAdmins("Bearer token");

    ResponseEntity<?> response = adminController.getAllAdmins("Bearer token");

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error: No admins found", response.getBody());
  }

  @Test
  @DisplayName("Should return 500 on internal error")
  void testGetAllAdminsException() {
    doThrow(new RuntimeException("Unexpected")).when(adminService).getAllAdmins("Bearer token");

    ResponseEntity<?> response = adminController.getAllAdmins("Bearer token");

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertFalse((Boolean) response.getBody());
  }

  @Test
  @DisplayName("activateAdmin returns 200 OK when successful")
  void activateAdmin_Success() {
    ResponseEntity<?> response = adminController.activateAdmin("validToken", "newPassword");
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(true, response.getBody());
  }

  @Test
  @DisplayName("activateAdmin returns 400 BAD_REQUEST on IllegalArgumentException")
  void activateAdmin_BadRequest() {
    doThrow(new IllegalArgumentException()).when(verificationService).activateAdmin(anyString(), anyString());
    ResponseEntity<?> response = adminController.activateAdmin("validToken", "newPassword");
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(false, response.getBody());
  }

  @Test
  @DisplayName("activateAdmin returns 401 UNAUTHORIZED on UnauthorizedException")
  void activateAdmin_Unauthorized() {
    doThrow(new UnauthorizedException("Not authorized")).when(verificationService).activateAdmin(anyString(), anyString());
    ResponseEntity<?> response = adminController.activateAdmin("validToken", "newPassword");
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals(false, response.getBody());
  }

  @Test
  @DisplayName("activateAdmin returns 500 INTERNAL_SERVER_ERROR on generic exception")
  void activateAdmin_ServerError() {
    doThrow(new RuntimeException()).when(verificationService).activateAdmin(anyString(), anyString());
    ResponseEntity<?> response = adminController.activateAdmin("validToken", "newPassword");
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(false, response.getBody());
  }

  @Test
  @DisplayName("sendMeTheCode returns 200 OK when successful")
  void sendMeTheCode_Success() {
    AdminLoginRequest loginRequest = new AdminLoginRequest("admin", "", "");
    ResponseEntity<?> response = adminController.sendMeTheCode(loginRequest);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(true, response.getBody());
  }

  @Test
  @DisplayName("sendMeTheCode returns 400 BAD_REQUEST on IllegalArgumentException")
  void sendMeTheCode_BadRequest() {
    doThrow(new IllegalArgumentException()).when(adminService).send2FAToken(anyString());
    ResponseEntity<?> response = adminController.sendMeTheCode(new AdminLoginRequest("admin", "", ""));
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("Invalid admin data"));
  }

  @Test
  @DisplayName("sendMeTheCode returns 401 UNAUTHORIZED on UnauthorizedException")
  void sendMeTheCode_Unauthorized() {
    doThrow(new UnauthorizedException("Unauthorized")).when(adminService).send2FAToken(anyString());
    ResponseEntity<?> response = adminController.sendMeTheCode(new AdminLoginRequest("admin", "", ""));
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("Unauthorized"));
  }

  @Test
  @DisplayName("sendMeTheCode returns 404 NOT_FOUND on UserNotFoundException")
  void sendMeTheCode_NotFound() {
    doThrow(new UserNotFoundException("Admin not found")).when(adminService).send2FAToken(anyString());
    ResponseEntity<?> response = adminController.sendMeTheCode(new AdminLoginRequest("admin", "", ""));
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("Admin not found"));
  }

  @Test
  @DisplayName("sendMeTheCode returns 401 UNAUTHORIZED on UserNotVerifiedException")
  void sendMeTheCode_NotVerified() {
    doThrow(new UserNotVerifiedException("Account not verified")).when(adminService).send2FAToken(anyString());
    ResponseEntity<?> response = adminController.sendMeTheCode(new AdminLoginRequest("admin", "", ""));
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("Account not verified"));
  }

  @Test
  @DisplayName("sendMeTheCode returns 500 INTERNAL_SERVER_ERROR on generic exception")
  void sendMeTheCode_ServerError() {
    doThrow(new RuntimeException()).when(adminService).send2FAToken(anyString());
    ResponseEntity<?> response = adminController.sendMeTheCode(new AdminLoginRequest("admin", "", ""));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("unexpected error"));
  }

}
