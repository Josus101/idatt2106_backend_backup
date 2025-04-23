package org.ntnu.idatt2106.backend.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserLoginDTOTest {

  @Test
  @DisplayName("Test UserLoginDTO constructor sets fields correctly")
  void  testConstructorSetsFields() {
    UserLoginDTO dto = new UserLoginDTO(
        "urekmazino@gmail.com",
        "password123"
    );
    assertEquals(dto.getEmail(), "urekmazino@gmail.com");
    assertEquals(dto.getPassword(), "password123");
  }
  @Test
  @DisplayName("Test UserLoginDTO getters and setters")
  void testGettersAndSetters() {
    UserLoginDTO dto = new UserLoginDTO(
        "urekmazino@gmail.com",
        "password123"
    );
    dto.setEmail("luslec@fug.no");
    dto.setPassword("password456");
    assertEquals(dto.getEmail(), "luslec@fug.no");
    assertEquals(dto.getPassword(), "password456");
  }

}
