package org.ntnu.idatt2106.backend.models;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TypeTest {

  @Test
  @DisplayName("Test Get Id method gets id correctly")
  void getIdReturnsCorrectly() {
    Type type = new Type();
    type.setId(1);
    assertEquals(1, type.getId());
  }

  @Test
  @DisplayName("Test Get Name method gets name correctly")
  void getNameReturnsCorrectly() {
    Type type = new Type();
    type.setName("Test Type");
    assertEquals("Test Type", type.getName());
  }
  @Test
  @DisplayName("Test Set Name method sets name correctly")
  void setNameSetsCorrectly() {
    Type type = new Type();
    type.setName("Test Type");
    assertEquals("Test Type", type.getName());
  }
  @Test
  @DisplayName("Test Set Id method sets id correctly")
  void setIdSetsCorrectly() {
    Type type = new Type();
    type.setId(1);
    assertEquals(1, type.getId());
  }
  @Test
  @DisplayName("Test Type constructor sets fields correctly")
  void testConstructorSetsFields() {
    Type type = new Type("Test Type", null);
    assertEquals("Test Type", type.getName());
    assertNull(type.getServices());
  }

  @Test
  @DisplayName("Test Type constructor with services sets fields correctly")
  void testConstructorWithServicesSetsFields() {
    EmergencyService service = new EmergencyService("Test Service", 11.4444, 22.5555, null);
    List<EmergencyService> services = List.of(service);
    Type type = new Type("Test Type", services);
    assertEquals("Test Type", type.getName());
    assertEquals(1, type.getServices().size());
    assertEquals(service, type.getServices().get(0));
  }

  @Test
  @DisplayName("Test Set Services method sets services correctly")
  void setServicesSetsCorrectly() {
    Type type = new Type();
    EmergencyService service = new EmergencyService("Test Service", 11.4444, 22.5555, null);
    List<EmergencyService> services = List.of(service);
    type.setServices(services);
    assertEquals(1, type.getServices().size());
    assertEquals(service, type.getServices().get(0));
  }

  @Test
  @DisplayName("Test Get Services method gets services correctly")
  void getServicesReturnsCorrectly() {
    Type type = new Type();
    EmergencyService service = new EmergencyService("Test Service", 11.4444, 22.5555, null);
    List<EmergencyService> services = List.of(service);
    type.setServices(services);
    assertEquals(1, type.getServices().size());
    assertEquals(service, type.getServices().get(0));
  }

}