package org.ntnu.idatt2106.backend.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ntnu.idatt2106.backend.dto.user.UserTokenResponse;

import static org.junit.jupiter.api.Assertions.*;

class UserTokenResponseTest {

  @Test
  @DisplayName("Test UserTokenResponse constructor with all arguments")
  void testAllArgsConstructor() {
    UserTokenResponse dto = new UserTokenResponse("abc123", 3600L);

    assertEquals("abc123", dto.getToken());
    assertEquals(3600L, dto.getExpirationTime());
  }

  @Test
  @DisplayName("Test UserTokenResponse getters and setters")
  void testGettersAndSetters() {
    UserTokenResponse dto = new UserTokenResponse("", 0L);
    dto.setToken("xyz789");
    dto.setExpirationTime(7200L);
    assertEquals("xyz789", dto.getToken());
    assertEquals(7200L, dto.getExpirationTime());
  }
}
