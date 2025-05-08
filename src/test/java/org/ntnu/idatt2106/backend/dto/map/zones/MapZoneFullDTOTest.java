package org.ntnu.idatt2106.backend.dto.map.zones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ntnu.idatt2106.backend.dto.map.CoordinatesDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for the MapZoneFullDTO class.
 */
public class MapZoneFullDTOTest {

  private MapZoneFullDTO mapZoneFullDTO;

  @BeforeEach
  void setUp() {
    mapZoneFullDTO = new MapZoneFullDTO(
        1L,
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
  @DisplayName("Test the constructor of MapZoneFullDTO")
  void testConstructor() {
    assertNotNull(mapZoneFullDTO, "MapZoneFullDTO should not be null");
    assertEquals(1L, mapZoneFullDTO.getId(), "ID mismatch");
    assertEquals("Test Zone", mapZoneFullDTO.getName(), "Name mismatch");
    assertEquals("Description for Test Zone", mapZoneFullDTO.getDescription(), "Description mismatch");
    assertEquals("Address 1", mapZoneFullDTO.getAddress(), "Address mismatch");
    assertEquals(1, mapZoneFullDTO.getSeverityLevel(), "Severity mismatch");
    assertEquals("Flood", mapZoneFullDTO.getType(), "Type mismatch");
    assertCoordinatesEqual(
        new CoordinatesDTO(63.424494, 10.439154),
        mapZoneFullDTO.getCoordinates()
    );
    assertEquals(
        "[[63.424494, 10.439154], [63.424694, 10.448154], [63.404494, 10.449154], [63.394494, 10.439154]]",
        mapZoneFullDTO.getPolygonCoordinateList(),
        "Border coordinates mismatch"
    );
  }


  @Test
  @DisplayName("Test the getters of MapZoneFullDTO")
  void testGetters() {
    assertEquals(1L, mapZoneFullDTO.getId(), "ID mismatch");
    assertEquals("Test Zone", mapZoneFullDTO.getName(), "Name mismatch");
    assertEquals("Description for Test Zone", mapZoneFullDTO.getDescription(), "Description mismatch");
    assertEquals("Address 1", mapZoneFullDTO.getAddress(), "Address mismatch");
    assertEquals(1, mapZoneFullDTO.getSeverityLevel(), "Severity mismatch");
    assertEquals("Flood", mapZoneFullDTO.getType(), "Type mismatch");
    assertCoordinatesEqual(
        new CoordinatesDTO(63.424494, 10.439154),
        mapZoneFullDTO.getCoordinates()
    );
    assertEquals(
        "[[63.424494, 10.439154], [63.424694, 10.448154], [63.404494, 10.449154], [63.394494, 10.439154]]",
        mapZoneFullDTO.getPolygonCoordinateList(),
        "Border coordinates mismatch"
    );
  }


  @Test
  @DisplayName("Test the setters of MapZoneFullDTO")
  void testSetters() {
    mapZoneFullDTO.setId(2L);
    mapZoneFullDTO.setName("Updated Zone");
    mapZoneFullDTO.setDescription("Updated description");
    mapZoneFullDTO.setAddress("Updated address");
    mapZoneFullDTO.setSeverityLevel(2);
    mapZoneFullDTO.setType("Fire");
    mapZoneFullDTO.setCoordinates(new CoordinatesDTO(63.424694, 10.448154));
    mapZoneFullDTO.setPolygonCoordinateList("[[63.424694, 10.448154], [63.424494, 10.439154]]");

    assertEquals(2L, mapZoneFullDTO.getId(), "ID mismatch after setter");
    assertEquals("Updated Zone", mapZoneFullDTO.getName(), "Name mismatch after setter");
    assertEquals("Updated description", mapZoneFullDTO.getDescription(), "Description mismatch after setter");
    assertEquals("Updated address", mapZoneFullDTO.getAddress(), "Address mismatch after setter");
    assertEquals(2, mapZoneFullDTO.getSeverityLevel(), "Severity mismatch after setter");
    assertEquals("Fire", mapZoneFullDTO.getType(), "Type mismatch after setter");
    assertCoordinatesEqual(
        new CoordinatesDTO(63.424694, 10.448154),
        mapZoneFullDTO.getCoordinates()
    );
    assertEquals(
        "[[63.424694, 10.448154], [63.424494, 10.439154]]",
        mapZoneFullDTO.getPolygonCoordinateList(),
        "Border coordinates mismatch after setter"
    );
  }

  private void assertCoordinatesEqual(CoordinatesDTO expected, CoordinatesDTO actual) {
    assertEquals(expected.getLatitude(), actual.getLatitude(), 0.000001, "Latitude mismatch");
    assertEquals(expected.getLongitude(), actual.getLongitude(), 0.000001, "Longitude mismatch");
  }
}
