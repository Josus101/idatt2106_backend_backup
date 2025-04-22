package org.ntnu.idatt2106.backend.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTokenDTOTest {

  @Test
  @DisplayName("Test UserTokenDTO constructor with all arguments")
  void testAllArgsConstructor() {
    UserTokenDTO dto = new UserTokenDTO("abc123", 3600L);

    assertEquals("abc123", dto.getToken());
    assertEquals(3600L, dto.getExpirationTime());
  }

  @Test
  @DisplayName("Test UserTokenDTO getters and setters")
  void testGettersAndSetters() {
    UserTokenDTO dto = new UserTokenDTO("", 0L);
    dto.setToken("xyz789");
    dto.setExpirationTime(7200L);
    assertEquals("xyz789", dto.getToken());
    assertEquals(7200L, dto.getExpirationTime());
  }
}
