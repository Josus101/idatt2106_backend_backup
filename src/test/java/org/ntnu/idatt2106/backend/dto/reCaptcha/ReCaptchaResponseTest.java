package org.ntnu.idatt2106.backend.dto.reCaptcha;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReCaptchaResponseTest {

  @Test
  @DisplayName("Test ReCaptchaResponse constructor with all arguments")
  void testAllArgsConstructor() {
    List<String> errorCodes = List.of("invalid-input-response");
    ReCaptchaResponse response = new ReCaptchaResponse(true, "2025-04-23T12:34:56Z", "yourdomain.com", errorCodes);

    assertTrue(response.isSuccess());
    assertEquals("2025-04-23T12:34:56Z", response.getChallenge_ts());
    assertEquals("yourdomain.com", response.getHostname());
    assertEquals(errorCodes, response.getErrorCodes());
  }

  @Test
  @DisplayName("Test ReCaptchaResponse getters and setters")
  void testGettersAndSetters() {
    ReCaptchaResponse response = new ReCaptchaResponse(false, null, null, null);

    response.setSuccess(true);
    response.setChallenge_ts("2025-04-23T10:00:00Z");
    response.setHostname("example.com");
    response.setErrorCodes(List.of("missing-input-secret", "bad-request"));

    assertTrue(response.isSuccess());
    assertEquals("2025-04-23T10:00:00Z", response.getChallenge_ts());
    assertEquals("example.com", response.getHostname());
    assertEquals(2, response.getErrorCodes().size());
    assertEquals("missing-input-secret", response.getErrorCodes().get(0));
  }
}
