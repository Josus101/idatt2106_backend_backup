package org.ntnu.idatt2106.backend.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmergencyServiceTest {
  Type type;

  @BeforeEach
  void setUp() {
    type = new Type("Test Type", null);
  }

  @Test
  @DisplayName("Test EmergencyService constructor sets fields correctly")
  void testConstructorSetsFields() {
    EmergencyService emergencyService = new EmergencyService(
        "Test Service",
        11.4444,
        22.5555,
        type
    );
    assertEquals("Test Service", emergencyService.getName());
    assertEquals(11.4444, emergencyService.getLatitude());
    assertEquals(22.5555, emergencyService.getLongitude());
    assertEquals(type, emergencyService.getType());
  }

  @Test
  @DisplayName("Test Get Id method gets id correctly")
  void getIdReturnsCorrectly() {
    EmergencyService emergencyService = new EmergencyService();
    emergencyService.setId(1);
    assertEquals(1, emergencyService.getId());
  }

  @Test
  @DisplayName("Test Get Name method gets name correctly")
  void getNameReturnsCorrectly() {
    EmergencyService emergencyService = new EmergencyService();
    emergencyService.setName("Test Service");
    assertEquals("Test Service", emergencyService.getName());
  }

  @Test
  @DisplayName("Test Set Name method sets name correctly")
  void setNameSetsCorrectly() {
    EmergencyService emergencyService = new EmergencyService();
    emergencyService.setName("Test Service");
    assertEquals("Test Service", emergencyService.getName());
  }

  @Test
  @DisplayName("Test Get Latitude method gets latitude correctly")
  void getLatitudeReturnsCorrectly() {
    EmergencyService emergencyService = new EmergencyService();
    emergencyService.setLatitude(11.4444);
    assertEquals(11.4444, emergencyService.getLatitude());
  }

  @Test
  @DisplayName("Test Set Latitude method sets latitude correctly")
  void setLatitudeSetsCorrectly() {
    EmergencyService emergencyService = new EmergencyService();
    emergencyService.setLatitude(11.4444);
    assertEquals(11.4444, emergencyService.getLatitude());
  }

  @Test
  @DisplayName("Test Get Longitude method gets longitude correctly")
  void getLongitudeReturnsCorrectly() {
    EmergencyService emergencyService = new EmergencyService();
    emergencyService.setLongitude(22.5555);
    assertEquals(22.5555, emergencyService.getLongitude());
  }

  @Test
  @DisplayName("Test Set Longitude method sets longitude correctly")
  void setLongitudeSetsCorrectly() {
    EmergencyService emergencyService = new EmergencyService();
    emergencyService.setLongitude(22.5555);
    assertEquals(22.5555, emergencyService.getLongitude());
  }

  @Test
  @DisplayName("Test Get Type method gets type correctly")
  void getTypeReturnsCorrectly() {
    EmergencyService emergencyService = new EmergencyService();
    emergencyService.setType(type);
    assertEquals(type, emergencyService.getType());
  }

  @Test
  @DisplayName("Test Set Type method sets type correctly")
  void setTypeSetsCorrectly() {
    EmergencyService emergencyService = new EmergencyService();
    emergencyService.setType(type);
    assertEquals(type, emergencyService.getType());
  }



}