package org.ntnu.idatt2106.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.ntnu.idatt2106.backend.service.ReCaptchaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReCaptchaControllerTest {

  @InjectMocks
  private ReCaptchaController reCaptchaController;

  @Mock
  private ReCaptchaService reCaptchaService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(reCaptchaController).build();
  }

  @Test
  @DisplayName("Test handleForm returns 200 OK on successful verification")
  void testHandleFormSuccess() throws Exception {
    when(reCaptchaService.verifyToken("valid-token")).thenReturn(true);

    ResponseEntity<String> response = reCaptchaController.handleForm("valid-token");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Captcha verification passed", response.getBody());
  }

  @Test
  @DisplayName("Test handleForm returns 403 FORBIDDEN")
  void testHandleFormFailure() throws Exception {
    when(reCaptchaService.verifyToken("invalid-token")).thenReturn(false);

    ResponseEntity<String> response = reCaptchaController.handleForm("invalid-token");

    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertEquals("Error: Captcha verification failed", response.getBody());
  }
}
