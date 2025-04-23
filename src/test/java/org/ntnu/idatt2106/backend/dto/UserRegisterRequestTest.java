package org.ntnu.idatt2106.backend.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ntnu.idatt2106.backend.dto.user.UserRegisterRequest;

public class UserRegisterRequestTest {

  @Test
  @DisplayName("Test UserRegisterRequest constructor sets fields correctly")
  void testConstructorSetsFieldsCorrectly() {
    UserRegisterRequest dto = new UserRegisterRequest(
        "kallekontainer@gmail.com",
        "password123",
        "Kalle",
        "Kontainer",
        "12345678"
    );
    assertEquals(dto.getEmail(), "kallekontainer@gmail.com");
    assertEquals(dto.getPassword(), "password123");
    assertEquals(dto.getFirstname(), "Kalle");
    assertEquals(dto.getLastname(), "Kontainer");
    assertEquals(dto.getPhoneNumber(), "12345678");
  }

  @Test
  @DisplayName("Test UserRegisterRequest getters and setters")
  void testGettersAndSetters() {
    UserRegisterRequest dto = new UserRegisterRequest("","","","","");
    dto.setEmail("karekartong@gmail.com");
    dto.setPassword("password123");
    dto.setFirstname("Kåre");
    dto.setLastname("Pappkartong");
    dto.setPhoneNumber("12345678");

    assertEquals(dto.getEmail(), "karekartong@gmail.com");
    assertEquals(dto.getPassword(), "password123");
    assertEquals(dto.getFirstname(), "Kåre");
    assertEquals(dto.getLastname(), "Pappkartong");
    assertEquals(dto.getPhoneNumber(), "12345678");
  }
}
