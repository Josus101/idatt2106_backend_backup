package org.ntnu.idatt2106.backend.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserRegisterDTOTest {

  @Test
  @DisplayName("Test UserRegisterDTO constructor sets fields correctly")
  void testConstructorSetsFieldsCorrectly() {
    UserRegisterDTO dto = new UserRegisterDTO(
        "kallekontainer@gmail.com",
        "password123",
        "Kalle",
        "Kontainer",
        "12345678"
    );
    assertEquals(dto.getEmail(), "kallekontainer@gmail.com");
    assertEquals(dto.getPassword(), "password123");
    assertEquals(dto.getFirstname(), "Kalle");
    assertEquals(dto.getSurname(), "Kontainer");
    assertEquals(dto.getPhoneNumber(), "12345678");
  }

  @Test
  @DisplayName("Test UserRegisterDTO getters and setters")
  void testGettersAndSetters() {
    UserRegisterDTO dto = new UserRegisterDTO("","","","","");
    dto.setEmail("karekartong@gmail.com");
    dto.setPassword("password123");
    dto.setFirstname("Kåre");
    dto.setSurname("Pappkartong");
    dto.setPhoneNumber("12345678");

    assertEquals(dto.getEmail(), "karekartong@gmail.com");
    assertEquals(dto.getPassword(), "password123");
    assertEquals(dto.getFirstname(), "Kåre");
    assertEquals(dto.getSurname(), "Pappkartong");
    assertEquals(dto.getPhoneNumber(), "12345678");
  }
}
