package org.ntnu.idatt2106.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.ntnu.idatt2106.backend.dto.admin.AdminLoginRegisterRequest;
import org.ntnu.idatt2106.backend.exceptions.UnauthorizedException;
import org.ntnu.idatt2106.backend.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminControllerTest {

  @InjectMocks
  private AdminController adminController;

  @Mock
  private AdminService adminService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Should return 200 when admin is successfully created")
  void testAddAdminUserSuccess() {
    AdminLoginRegisterRequest dto = new AdminLoginRegisterRequest("admin", "test@mail.com", "securePass");

    ResponseEntity<Boolean> response = adminController.addAdminUser(dto, "Bearer token");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody());
    verify(adminService, times(1)).register("admin", "test@mail.com", "Bearer token");
  }

  @Test
  @DisplayName("Should return 401 if unauthorized during admin creation")
  void testAddAdminUserUnauthorized() {
    AdminLoginRegisterRequest dto = new AdminLoginRegisterRequest("admin", "test@mail.com","securePass");
    doThrow(new UnauthorizedException("Not allowed")).when(adminService).register(anyString(), anyString(), anyString());

    ResponseEntity<Boolean> response = adminController.addAdminUser(dto, "Bearer token");

    assertFalse(response.getBody());
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  @DisplayName("Should return 500 on exception during admin creation")
  void testAddAdminUserException() {
    AdminLoginRegisterRequest dto = new AdminLoginRegisterRequest("admin","test@mail.com", "securePass");
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
    AdminLoginRegisterRequest loginDTO = new AdminLoginRegisterRequest("admin","test@mail.com", "securePass");
    when(adminService.authenticate("admin", "securePass")).thenReturn("validToken");

    ResponseEntity<?> response = adminController.login(loginDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("validToken", response.getBody());
  }

  @Test
  @DisplayName("Should return 400 on invalid admin login")
  void testAdminLoginBadRequest() {
    AdminLoginRegisterRequest loginDTO = new AdminLoginRegisterRequest("admin","test@mail.com", "wrongPass");
    when(adminService.authenticate(any(), any()))
        .thenThrow(new IllegalArgumentException("Invalid credentials"));

    ResponseEntity<?> response = adminController.login(loginDTO);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Invalid admin data", response.getBody());
  }

  @Test
  @DisplayName("Should return 500 on exception during login")
  void testAdminLoginException() {
    AdminLoginRegisterRequest loginDTO = new AdminLoginRegisterRequest("admin","test@mail.com", "pass");
    when(adminService.authenticate(any(), any()))
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
}
