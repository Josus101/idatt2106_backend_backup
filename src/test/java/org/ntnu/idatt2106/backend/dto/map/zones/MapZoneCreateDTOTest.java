package org.ntnu.idatt2106.backend.dto.map.zones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ntnu.idatt2106.backend.dto.map.CoordinatesDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the MapZoneCreateDTO class.
 */
public class MapZoneCreateDTOTest {

  private MapZoneCreateDTO mapZoneCreateDTO;

  @BeforeEach
  void setUp() {
    mapZoneCreateDTO = new MapZoneCreateDTO(
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
  @DisplayName("Test the constructor of MapZoneCreateDTO")
  void testConstructor() {
    assertEquals("Test Zone", mapZoneCreateDTO.getName());
    assertEquals("Description for Test Zone", mapZoneCreateDTO.getDescription());
    assertEquals("Address 1", mapZoneCreateDTO.getAddress());
    assertEquals(1, mapZoneCreateDTO.getSeverityLevel());
    assertEquals("Flood", mapZoneCreateDTO.getType());
    assertCoordinatesEqual(new CoordinatesDTO(63.424494, 10.439154), mapZoneCreateDTO.getCoordinates());
    assertEquals(
        "[[63.424494, 10.439154], [63.424694, 10.448154], [63.404494, 10.449154], [63.394494, 10.439154]]",
        mapZoneCreateDTO.getPolygonCoordinateList()
    );
  }

  @Test
  @DisplayName("Test the getters of MapZoneCreateDTO")
  void testGetters() {
    assertEquals("Test Zone", mapZoneCreateDTO.getName());
    assertEquals("Description for Test Zone", mapZoneCreateDTO.getDescription());
    assertEquals("Address 1", mapZoneCreateDTO.getAddress());
    assertEquals(1, mapZoneCreateDTO.getSeverityLevel());
    assertEquals("Flood", mapZoneCreateDTO.getType());
    assertCoordinatesEqual(new CoordinatesDTO(63.424494, 10.439154), mapZoneCreateDTO.getCoordinates());
    assertEquals(
        "[[63.424494, 10.439154], [63.424694, 10.448154], [63.404494, 10.449154], [63.394494, 10.439154]]",
        mapZoneCreateDTO.getPolygonCoordinateList()
    );
  }

  @Test
  @DisplayName("Test the setters of MapZoneCreateDTO")
  void testSetters() {
    mapZoneCreateDTO.setName("Updated Zone");
    mapZoneCreateDTO.setDescription("Updated Description");
    mapZoneCreateDTO.setAddress("Updated Address");
    mapZoneCreateDTO.setSeverityLevel(2);
    mapZoneCreateDTO.setType("Fire");
    mapZoneCreateDTO.setCoordinates(new CoordinatesDTO(63.424694, 10.448154));
    mapZoneCreateDTO.setPolygonCoordinateList("[[63.424694, 10.448154], [63.424494, 10.439154]]");

    assertEquals("Updated Zone", mapZoneCreateDTO.getName());
    assertEquals("Updated Description", mapZoneCreateDTO.getDescription());
    assertEquals("Updated Address", mapZoneCreateDTO.getAddress());
    assertEquals(2, mapZoneCreateDTO.getSeverityLevel());
    assertEquals("Fire", mapZoneCreateDTO.getType());
    assertCoordinatesEqual(new CoordinatesDTO(63.424694, 10.448154), mapZoneCreateDTO.getCoordinates());
    assertEquals(
        "[[63.424694, 10.448154], [63.424494, 10.439154]]",
        mapZoneCreateDTO.getPolygonCoordinateList()
    );
  }

  private void assertCoordinatesEqual(CoordinatesDTO expected, CoordinatesDTO actual) {
    assertEquals(expected.getLatitude(), actual.getLatitude(), 0.000001, "Latitude mismatch");
    assertEquals(expected.getLongitude(), actual.getLongitude(), 0.000001, "Longitude mismatch");
  }
}
