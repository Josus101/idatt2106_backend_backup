package org.ntnu.idatt2106.backend.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TypeTest {

  @Test
  @DisplayName("Test full constructor sets all fields correctly")
  void testFullConstructor() {
    EmergencyService service1 = new EmergencyService();
    EmergencyService service2 = new EmergencyService();
    List<EmergencyService> services = List.of(service1, service2);

    Type type = new Type(1, "Fire", services);

    assertEquals(1, type.getId());
    assertEquals("Fire", type.getName());
    assertEquals(2, type.getServices().size());
  }

  @Test
  @DisplayName("Test constructor with id and name")
  void testConstructorWithIdAndName() {
    Type type = new Type(2, "Medical");

    assertEquals(2, type.getId());
    assertEquals("Medical", type.getName());
    assertNull(type.getServices());
  }

  @Test
  @DisplayName("Test constructor with name only")
  void testConstructorWithNameOnly() {
    Type type = new Type("Police");

    assertEquals(0, type.getId()); // defaulted to 0
    assertEquals("Police", type.getName());
    assertNull(type.getServices());
  }

  @Test
  @DisplayName("Test setId and getId")
  void testIdField() {
    Type type = new Type();
    type.setId(42);
    assertEquals(42, type.getId());
  }

  @Test
  @DisplayName("Test setName and getName")
  void testNameField() {
    Type type = new Type();
    type.setName("Rescue");
    assertEquals("Rescue", type.getName());
  }

  @Test
  @DisplayName("Test setServices and getServices")
  void testServicesField() {
    EmergencyService service1 = new EmergencyService();
    EmergencyService service2 = new EmergencyService();
    List<EmergencyService> services = List.of(service1, service2);

    Type type = new Type();
    type.setServices(services);

    assertEquals(2, type.getServices().size());
    assertTrue(type.getServices().contains(service1));
  }
}
