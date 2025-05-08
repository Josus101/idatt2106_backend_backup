package org.ntnu.idatt2106.backend.dto.map.zones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ntnu.idatt2106.backend.dto.map.CoordinatesDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the ZoneCreateDTO class.
 */
public class ZoneCreateDTOTest {

  private ZoneCreateDTO zoneCreateDTO;

  @BeforeEach
  void setUp() {
    zoneCreateDTO = new ZoneCreateDTO(
        "Test Zone",
        "Description for Test Zone",
        "Address 1",
        1,
        "Flood",
        new CoordinatesDTO(
            63.424494,
            10.439154
        ),
        "[[63.424494, 10.439154], [63.424694, 10.448154], [63.404494, 10.449154], [63.394494, 10.439154]]"
    );
  }

  @Test
  @DisplayName("Test the constructor of ZoneCreateDTO")
  void testConstructor() {
    assertEquals("Test Zone", zoneCreateDTO.getName());
    assertEquals("Description for Test Zone", zoneCreateDTO.getDescription());
    assertEquals("Address 1", zoneCreateDTO.getAddress());
    assertEquals(1, zoneCreateDTO.getSeverityLevel());
    assertEquals("Flood", zoneCreateDTO.getType());
    assertCoordinatesEqual(new CoordinatesDTO(63.424494, 10.439154), zoneCreateDTO.getCoordinates());
    assertEquals(
        "[[63.424494, 10.439154], [63.424694, 10.448154], [63.404494, 10.449154], [63.394494, 10.439154]]",
        zoneCreateDTO.getPolygonCoordinateList()
    );
  }

  @Test
  @DisplayName("Test the getters of ZoneCreateDTO")
  void testGetters() {
    assertEquals("Test Zone", zoneCreateDTO.getName());
    assertEquals("Description for Test Zone", zoneCreateDTO.getDescription());
    assertEquals("Address 1", zoneCreateDTO.getAddress());
    assertEquals(1, zoneCreateDTO.getSeverityLevel());
    assertEquals("Flood", zoneCreateDTO.getType());
    assertCoordinatesEqual(new CoordinatesDTO(63.424494, 10.439154), zoneCreateDTO.getCoordinates());
    assertEquals(
        "[[63.424494, 10.439154], [63.424694, 10.448154], [63.404494, 10.449154], [63.394494, 10.439154]]",
        zoneCreateDTO.getPolygonCoordinateList()
    );
  }

  @Test
  @DisplayName("Test the setters of ZoneCreateDTO")
  void testSetters() {
    zoneCreateDTO.setName("Updated Zone");
    zoneCreateDTO.setDescription("Updated Description");
    zoneCreateDTO.setAddress("Updated Address");
    zoneCreateDTO.setSeverityLevel(2);
    zoneCreateDTO.setType("Fire");
    zoneCreateDTO.setCoordinates(new CoordinatesDTO(63.424694, 10.448154));
    zoneCreateDTO.setPolygonCoordinateList("[[63.424694, 10.448154], [63.424494, 10.439154]]");

    assertEquals("Updated Zone", zoneCreateDTO.getName());
    assertEquals("Updated Description", zoneCreateDTO.getDescription());
    assertEquals("Updated Address", zoneCreateDTO.getAddress());
    assertEquals(2, zoneCreateDTO.getSeverityLevel());
    assertEquals("Fire", zoneCreateDTO.getType());
    assertCoordinatesEqual(new CoordinatesDTO(63.424694, 10.448154), zoneCreateDTO.getCoordinates());
    assertEquals(
        "[[63.424694, 10.448154], [63.424494, 10.439154]]",
        zoneCreateDTO.getPolygonCoordinateList()
    );
  }

  private void assertCoordinatesEqual(CoordinatesDTO expected, CoordinatesDTO actual) {
    assertEquals(expected.getLatitude(), actual.getLatitude(), 0.000001, "Latitude mismatch");
    assertEquals(expected.getLongitude(), actual.getLongitude(), 0.000001, "Longitude mismatch");
  }
}
