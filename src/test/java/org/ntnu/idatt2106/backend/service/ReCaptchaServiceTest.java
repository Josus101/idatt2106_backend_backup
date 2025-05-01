package org.ntnu.idatt2106.backend.service;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.ntnu.idatt2106.backend.dto.reCaptcha.ReCaptchaResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ReCaptchaServiceTest {

  @InjectMocks
  private ReCaptchaService reCaptchaService;

  @Mock
  private RestTemplate restTemplate;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Should return true when reCaptcha token is successfully verified")
  void testVerifyReCaptchaToken_Success() {
    Map<String, Object> mockResponse = Map.of("success", true, "score", 0.9);
    when(restTemplate.postForEntity(
        anyString(),
        any(HttpEntity.class),
        eq(Map.class)
    )).thenReturn(ResponseEntity.ok(mockResponse));

    boolean result = reCaptchaService.verifyToken("valid-token");

    assertTrue(result);
  }

  @Test
  @DisplayName("Should return false when reCaptcha token is invalid")
  void testVerifyReCaptchaToken_Failure() {
    Map<String, Object> mockResponse = Map.of("success", false, "score", 0.3);
    when(restTemplate.postForEntity(
        anyString(),
        any(HttpEntity.class),
        eq(Map.class)
    )).thenReturn(ResponseEntity.ok(mockResponse));

    boolean result = reCaptchaService.verifyToken("invalid-token");

    assertFalse(result);
  }

  @Test
  @DisplayName("Should return false if response body is null")
  void testVerifyReCaptchaToken_NullBody() {
    when(restTemplate.postForEntity(
        anyString(),
        any(HttpEntity.class),
        eq(Map.class)
    )).thenReturn(ResponseEntity.ok(null));

    boolean result = reCaptchaService.verifyToken("null-response-token");

    assertFalse(result);
  }
}
