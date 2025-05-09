package org.ntnu.idatt2106.backend.dto.map.zones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ntnu.idatt2106.backend.dto.map.CoordinatesDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for the ZoneFullDTO class.
 */
public class ZoneFullDTOTest {

  private ZoneFullDTO zoneFullDTO;

  @BeforeEach
  void setUp() {
    zoneFullDTO = new ZoneFullDTO(
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
  @DisplayName("Test the constructor of ZoneFullDTO")
  void testConstructor() {
    assertNotNull(zoneFullDTO, "ZoneFullDTO should not be null");
    assertEquals(1L, zoneFullDTO.getId(), "ID mismatch");
    assertEquals("Test Zone", zoneFullDTO.getName(), "Name mismatch");
    assertEquals("Description for Test Zone", zoneFullDTO.getDescription(), "Description mismatch");
    assertEquals("Address 1", zoneFullDTO.getAddress(), "Address mismatch");
    assertEquals(1, zoneFullDTO.getSeverityLevel(), "Severity mismatch");
    assertEquals("Flood", zoneFullDTO.getType(), "Type mismatch");
    assertCoordinatesEqual(
        new CoordinatesDTO(63.424494, 10.439154),
        zoneFullDTO.getCoordinates()
    );
    assertEquals(
        "[[63.424494, 10.439154], [63.424694, 10.448154], [63.404494, 10.449154], [63.394494, 10.439154]]",
        zoneFullDTO.getPolygonCoordinateList(),
        "Border coordinates mismatch"
    );
  }


  @Test
  @DisplayName("Test the getters of ZoneFullDTO")
  void testGetters() {
    assertEquals(1L, zoneFullDTO.getId(), "ID mismatch");
    assertEquals("Test Zone", zoneFullDTO.getName(), "Name mismatch");
    assertEquals("Description for Test Zone", zoneFullDTO.getDescription(), "Description mismatch");
    assertEquals("Address 1", zoneFullDTO.getAddress(), "Address mismatch");
    assertEquals(1, zoneFullDTO.getSeverityLevel(), "Severity mismatch");
    assertEquals("Flood", zoneFullDTO.getType(), "Type mismatch");
    assertCoordinatesEqual(
        new CoordinatesDTO(63.424494, 10.439154),
        zoneFullDTO.getCoordinates()
    );
    assertEquals(
        "[[63.424494, 10.439154], [63.424694, 10.448154], [63.404494, 10.449154], [63.394494, 10.439154]]",
        zoneFullDTO.getPolygonCoordinateList(),
        "Border coordinates mismatch"
    );
  }


  @Test
  @DisplayName("Test the setters of ZoneFullDTO")
  void testSetters() {
    zoneFullDTO.setId(2L);
    zoneFullDTO.setName("Updated Zone");
    zoneFullDTO.setDescription("Updated description");
    zoneFullDTO.setAddress("Updated address");
    zoneFullDTO.setSeverityLevel(2);
    zoneFullDTO.setType("Fire");
    zoneFullDTO.setCoordinates(new CoordinatesDTO(63.424694, 10.448154));
    zoneFullDTO.setPolygonCoordinateList("[[63.424694, 10.448154], [63.424494, 10.439154]]");

    assertEquals(2L, zoneFullDTO.getId(), "ID mismatch after setter");
    assertEquals("Updated Zone", zoneFullDTO.getName(), "Name mismatch after setter");
    assertEquals("Updated description", zoneFullDTO.getDescription(), "Description mismatch after setter");
    assertEquals("Updated address", zoneFullDTO.getAddress(), "Address mismatch after setter");
    assertEquals(2, zoneFullDTO.getSeverityLevel(), "Severity mismatch after setter");
    assertEquals("Fire", zoneFullDTO.getType(), "Type mismatch after setter");
    assertCoordinatesEqual(
        new CoordinatesDTO(63.424694, 10.448154),
        zoneFullDTO.getCoordinates()
    );
    assertEquals(
        "[[63.424694, 10.448154], [63.424494, 10.439154]]",
        zoneFullDTO.getPolygonCoordinateList(),
        "Border coordinates mismatch after setter"
    );
  }

  private void assertCoordinatesEqual(CoordinatesDTO expected, CoordinatesDTO actual) {
    assertEquals(expected.getLatitude(), actual.getLatitude(), 0.000001, "Latitude mismatch");
    assertEquals(expected.getLongitude(), actual.getLongitude(), 0.000001, "Longitude mismatch");
  }
}
