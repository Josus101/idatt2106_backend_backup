package org.ntnu.idatt2106.backend.dto.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ntnu.idatt2106.backend.dto.user.UserLoginRequest;

public class UserLoginRequestTest {

  @Test
  @DisplayName("Test UserLoginRequest constructor sets fields correctly")
  void  testConstructorSetsFields() {
    UserLoginRequest dto = new UserLoginRequest(
        "urekmazino@gmail.com",
        "password123"
    );
    assertEquals(dto.getEmail(), "urekmazino@gmail.com");
    assertEquals(dto.getPassword(), "password123");
  }
  @Test
  @DisplayName("Test UserLoginRequest getters and setters")
  void testGettersAndSetters() {
    UserLoginRequest dto = new UserLoginRequest(
        "urekmazino@gmail.com",
        "password123"
    );
    dto.setEmail("luslec@fug.no");
    dto.setPassword("password456");
    assertEquals(dto.getEmail(), "luslec@fug.no");
    assertEquals(dto.getPassword(), "password456");
  }

}
