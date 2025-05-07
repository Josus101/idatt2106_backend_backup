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
      new CoordinatesDTO(63.424494, 10.439154),
      "Flood",
      2,
      List.of(
        List.of(
          List.of(
              new CoordinatesDTO(63.424494, 10.439154),
              new CoordinatesDTO(63.424694, 10.448154),
              new CoordinatesDTO(63.404494, 10.449154),
              new CoordinatesDTO(63.394494, 10.439154)
          )
        )
      )
    );
  }

  @Test
  @DisplayName("Test the constructor of MapZoneFullDTO")
  void testConstructor() {
    assertNotNull(mapZoneFullDTO);

    assertEquals(1L, mapZoneFullDTO.getId());
    assertEquals("Test Zone", mapZoneFullDTO.getName());
    assertEquals("Description for Test Zone", mapZoneFullDTO.getDescription());
    assertEquals("Address 1", mapZoneFullDTO.getAddress());
    assertCoordinatesEqual(new CoordinatesDTO(63.424494, 10.439154), mapZoneFullDTO.getCoordinates());
    assertEquals("Flood", mapZoneFullDTO.getType());
    assertEquals(2, mapZoneFullDTO.getSeverityLevel());

    List<List<List<CoordinatesDTO>>> expectedPolygon = List.of(List.of(
        List.of(
            new CoordinatesDTO(63.424494, 10.439154),
            new CoordinatesDTO(63.424694, 10.448154),
            new CoordinatesDTO(63.404494, 10.449154),
            new CoordinatesDTO(63.394494, 10.439154)
        )
    ));
    assertPolygonCoordinatesEqual(expectedPolygon, mapZoneFullDTO.getPolygonCoordinates());
  }


  @Test
  @DisplayName("Test the getters of MapZoneFullDTO")
  void testGetters() {
    assertEquals(1L, mapZoneFullDTO.getId());
    assertEquals("Test Zone", mapZoneFullDTO.getName());
    assertEquals("Description for Test Zone", mapZoneFullDTO.getDescription());
    assertEquals("Address 1", mapZoneFullDTO.getAddress());
    assertCoordinatesEqual(new CoordinatesDTO(63.424494, 10.439154), mapZoneFullDTO.getCoordinates());
    assertEquals("Flood", mapZoneFullDTO.getType());
    assertEquals(2, mapZoneFullDTO.getSeverityLevel());

    List<List<List<CoordinatesDTO>>> expectedPolygon = List.of(List.of(
        List.of(
            new CoordinatesDTO(63.424494, 10.439154),
            new CoordinatesDTO(63.424694, 10.448154),
            new CoordinatesDTO(63.404494, 10.449154),
            new CoordinatesDTO(63.394494, 10.439154)
        )
    ));
    assertPolygonCoordinatesEqual(expectedPolygon, mapZoneFullDTO.getPolygonCoordinates());
  }


  @Test
  @DisplayName("Test the setters of MapZoneFullDTO")
  void testSetters() {
    mapZoneFullDTO.setId(2L);
    mapZoneFullDTO.setName("Updated Zone");
    mapZoneFullDTO.setDescription("Updated description");
    mapZoneFullDTO.setAddress("Updated address");
    mapZoneFullDTO.setCoordinates(new CoordinatesDTO(63.424693, 10.448153));
    mapZoneFullDTO.setType("Fire");
    mapZoneFullDTO.setSeverityLevel(3);

    List<List<List<CoordinatesDTO>>> updatedPolygon = List.of(List.of(
        List.of(
            new CoordinatesDTO(63.424693, 10.448153),
            new CoordinatesDTO(63.424694, 10.448154),
            new CoordinatesDTO(63.404494, 10.449154),
            new CoordinatesDTO(63.394494, 10.439154)
        )
    ));
    mapZoneFullDTO.setPolygonCoordinates(updatedPolygon);

    assertEquals(2L, mapZoneFullDTO.getId());
    assertEquals("Updated Zone", mapZoneFullDTO.getName());
    assertEquals("Updated description", mapZoneFullDTO.getDescription());
    assertEquals("Updated address", mapZoneFullDTO.getAddress());
    assertCoordinatesEqual(new CoordinatesDTO(63.424693, 10.448153), mapZoneFullDTO.getCoordinates());
    assertEquals("Fire", mapZoneFullDTO.getType());
    assertEquals(3, mapZoneFullDTO.getSeverityLevel());

    assertPolygonCoordinatesEqual(updatedPolygon, mapZoneFullDTO.getPolygonCoordinates());
  }

  private void assertPolygonCoordinatesEqual(
      List<List<List<CoordinatesDTO>>> expected,
      List<List<List<CoordinatesDTO>>> actual
  ) {
    assertEquals(expected.size(), actual.size(), "Outer list size mismatch");

    for (int i = 0; i < expected.size(); i++) {
      List<List<CoordinatesDTO>> expectedList = expected.get(i);
      List<List<CoordinatesDTO>> actualList = actual.get(i);
      assertEquals(expectedList.size(), actualList.size(), "Middle list size mismatch at index " + i);

      for (int j = 0; j < expectedList.size(); j++) {
        List<CoordinatesDTO> expectedCoords = expectedList.get(j);
        List<CoordinatesDTO> actualCoords = actualList.get(j);
        assertEquals(expectedCoords.size(), actualCoords.size(), "Inner list size mismatch at index " + i + "," + j);

        for (int k = 0; k < expectedCoords.size(); k++) {
          assertCoordinatesEqual(expectedCoords.get(k), actualCoords.get(k));
        }
      }
    }
  }

  private void assertCoordinatesEqual(CoordinatesDTO expected, CoordinatesDTO actual) {
    assertEquals(expected.getLatitude(), actual.getLatitude(), 0.000001, "Latitude mismatch");
    assertEquals(expected.getLongitude(), actual.getLongitude(), 0.000001, "Longitude mismatch");
  }
}
