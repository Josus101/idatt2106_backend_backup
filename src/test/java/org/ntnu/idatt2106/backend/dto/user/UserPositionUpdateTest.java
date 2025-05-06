package org.ntnu.idatt2106.backend.dto.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserPositionUpdateTest {

  @Test
  @DisplayName("Test getLatitude returns correct value")
  void getLatitude() {
    UserPositionUpdate userPositionUpdate = new UserPositionUpdate();
    userPositionUpdate.setLatitude(69.4);
    assertEquals(69.4, userPositionUpdate.getLatitude());
  }

  @Test
  @DisplayName("Test getLongitude returns correct value")
  void getLongitude() {
    UserPositionUpdate userPositionUpdate = new UserPositionUpdate();
    userPositionUpdate.setLongitude(18.9);
    assertEquals(18.9, userPositionUpdate.getLongitude());
  }

  @Test
  @DisplayName("Test setLatitude sets correct value")
  void setLatitude() {
    UserPositionUpdate userPositionUpdate = new UserPositionUpdate();
    userPositionUpdate.setLatitude(69.4);
    assertEquals(69.4, userPositionUpdate.getLatitude());
  }

  @Test
  @DisplayName("Test setLongitude sets correct value")
  void setLongitude() {
    UserPositionUpdate userPositionUpdate = new UserPositionUpdate();
    userPositionUpdate.setLongitude(18.9);
    assertEquals(18.9, userPositionUpdate.getLongitude());
  }
}