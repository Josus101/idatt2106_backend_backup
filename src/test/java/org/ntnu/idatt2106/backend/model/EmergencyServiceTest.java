package org.ntnu.idatt2106.backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmergencyServiceTest {

  EmergencyService testEmergencyService;

  @BeforeEach
  void setUp() {
    testEmergencyService = new EmergencyService();
  }

  @Test
  @DisplayName("Test EmergencyService constructor sets fields correctly")
  void testConstructorSetsFields() {
    EmergencyService emergencyService = new EmergencyService(
        "Test Service",
        "Test Description",
        10.0,
        20.0,
        "Test LocalID",
        new Type("Test Type")
    );

    assertEquals("Test Service", emergencyService.getName());
    assertEquals(10.0, emergencyService.getLatitude());
    assertEquals(20.0, emergencyService.getLongitude());
    assertEquals("Test Type", emergencyService.getType().getName());
  }

  @Test
  @DisplayName("Test EmergencyService empty constructor")
  void testEmptyConstructor() {
    EmergencyService emergencyService = new EmergencyService();
    assertNotNull(emergencyService);
  }

  @Test
  @DisplayName("Test EmergencyService full constructor")
  void testFullConstructor() {
    EmergencyService emergencyService = new EmergencyService(
        "Test Service",
        "Test Description",
        10.0,
        20.0,
        "Test LocalID",
        new Type("Test Type")
    );

    assertEquals("Test Service", emergencyService.getName());
    assertEquals(10.0, emergencyService.getLatitude());
    assertEquals(20.0, emergencyService.getLongitude());
    assertEquals("Test Type", emergencyService.getType().getName());
  }

  @Test
  @DisplayName("Test EmergencyService name field")
  void testIdField() {
    EmergencyService emergencyService = new EmergencyService();
    emergencyService.setId(1);
    assertEquals(1, emergencyService.getId());
  }

  @Test
  @DisplayName("Test EmergencyService name field")
  void testNameField() {
    EmergencyService emergencyService = new EmergencyService();
    emergencyService.setName("Test Service");
    assertEquals("Test Service", emergencyService.getName());
  }

  @Test
  @DisplayName("Test EmergencyService latitude field")
  void testLatitudeField() {
    EmergencyService emergencyService = new EmergencyService();
    emergencyService.setLatitude(10.0);
    assertEquals(10.0, emergencyService.getLatitude());
  }

  @Test
  @DisplayName("Test EmergencyService longitude field")
  void testLongitudeField() {
    EmergencyService emergencyService = new EmergencyService();
    emergencyService.setLongitude(20.0);
    assertEquals(20.0, emergencyService.getLongitude());
  }

  @Test
  @DisplayName("Test EmergencyService type field")
  void testTypeField() {
    EmergencyService emergencyService = new EmergencyService();
    Type type = new Type("Test Type");
    emergencyService.setType(type);
    assertEquals(type, emergencyService.getType());
  }

}