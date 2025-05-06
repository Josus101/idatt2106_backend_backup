package org.ntnu.idatt2106.backend.dto.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserPositionResponseTest {

  @Test
  @DisplayName("Test getLatitude returns correct value")
  void getLatitude() {
    UserPositionResponse userPositionResponse = new UserPositionResponse();
    userPositionResponse.setLatitude(69.4);
    assertEquals(69.4, userPositionResponse.getLatitude());
  }

  @Test
  @DisplayName("Test getLongitude returns correct value")
  void getLongitude() {
    UserPositionResponse userPositionResponse = new UserPositionResponse();
    userPositionResponse.setLongitude(18.9);
    assertEquals(18.9, userPositionResponse.getLongitude());
  }

  @Test
  @DisplayName("Test getPositionUpdateTime returns correct value")
  void getPositionUpdateTime() {
    UserPositionResponse userPositionResponse = new UserPositionResponse();
    userPositionResponse.setPositionUpdateTime("2023-10-01T12:00:00Z");
    assertEquals("2023-10-01T12:00:00Z", userPositionResponse.getPositionUpdateTime());
  }

  @Test
  @DisplayName("Test getId returns correct value")
  void getId() {
    UserPositionResponse userPositionResponse = new UserPositionResponse();
    userPositionResponse.setId(1);
    assertEquals(1, userPositionResponse.getId());
  }

  @Test
  @DisplayName("Test getName returns correct value")
  void getName() {
    UserPositionResponse userPositionResponse = new UserPositionResponse();
    userPositionResponse.setName("Kuhn Aguero Agnes");
    assertEquals("Kuhn Aguero Agnes", userPositionResponse.getName());
  }

  @Test
  @DisplayName("Test setLatitude sets correct value")
  void setLatitude() {
    UserPositionResponse userPositionResponse = new UserPositionResponse();
    userPositionResponse.setLatitude(69.4);
    assertEquals(69.4, userPositionResponse.getLatitude());
  }

  @Test
  @DisplayName("Test setLongitude sets correct value")
  void setLongitude() {
    UserPositionResponse userPositionResponse = new UserPositionResponse();
    userPositionResponse.setLongitude(18.9);
    assertEquals(18.9, userPositionResponse.getLongitude());
  }

  @Test
  @DisplayName("Test setPositionUpdateTime sets correct value")
  void setPositionUpdateTime() {
    UserPositionResponse userPositionResponse = new UserPositionResponse();
    userPositionResponse.setPositionUpdateTime("2023-10-01T12:00:00Z");
    assertEquals("2023-10-01T12:00:00Z", userPositionResponse.getPositionUpdateTime());
  }

  @Test
  @DisplayName("Test setId sets correct value")
  void setId() {
    UserPositionResponse userPositionResponse = new UserPositionResponse();
    userPositionResponse.setId(1);
    assertEquals(1, userPositionResponse.getId());
  }

  @Test
  @DisplayName("Test setName sets correct value")
  void setName() {
    UserPositionResponse userPositionResponse = new UserPositionResponse();
    userPositionResponse.setName("Kuhn Aguero Agnes");
    assertEquals("Kuhn Aguero Agnes", userPositionResponse.getName());
  }
}