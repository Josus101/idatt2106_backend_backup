package org.ntnu.idatt2106.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.ntnu.idatt2106.backend.dto.reCaptcha.ReCaptchaResponse;
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

  /**
   * Tests verifyReCaptchaToken method to return true when result is success.
   */
  @Test
  @DisplayName("Should return true when reCaptcha token is successfully verified")
  void testVerifyReCaptchaToken_Success() {
    ReCaptchaResponse mockResponse = new ReCaptchaResponse(true, "2025-04-23T10:00:00Z", "localhost", null);
    when(restTemplate.postForEntity(
            anyString(),
            any(LinkedMultiValueMap.class),
            eq(ReCaptchaResponse.class)
    )).thenReturn(ResponseEntity.ok(mockResponse));

    boolean result = reCaptchaService.verifyReCaptchaToken("valid-token");

    assertTrue(result);
  }

  /**
   * Tests verifyReCaptchaToken method to return false when result is failure.
   */
  @Test
  @DisplayName("Should return false when reCaptcha token is invalid")
  void testVerifyReCaptchaToken_Failure() {
    ReCaptchaResponse mockResponse = new ReCaptchaResponse(false, "2025-04-23T10:00:00Z", "localhost", List.of("invalid-input-response"));
    when(restTemplate.postForEntity(
            anyString(),
            any(LinkedMultiValueMap.class),
            eq(ReCaptchaResponse.class)
    )).thenReturn(ResponseEntity.ok(mockResponse));

    boolean result = reCaptchaService.verifyReCaptchaToken("invalid-token");

    assertFalse(result);
  }

  /**
   * Tests verifyReCaptchaToken method to return false when result is null.
   */
  @Test
  @DisplayName("Should return false if response body is null")
  void testVerifyReCaptchaToken_NullBody() {
    when(restTemplate.postForEntity(
            anyString(),
            any(LinkedMultiValueMap.class),
            eq(ReCaptchaResponse.class)
    )).thenReturn(ResponseEntity.ok(null));

    boolean result = reCaptchaService.verifyReCaptchaToken("null-response-token");

    assertFalse(result);
  }
}
