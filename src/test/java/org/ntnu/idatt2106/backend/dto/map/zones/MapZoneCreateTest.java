package org.ntnu.idatt2106.backend.dto.map.zones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ntnu.idatt2106.backend.dto.map.CoordinatesDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapZoneCreateTest {

  private MapZoneCreateDTO mapZoneCreateDTO;

  @BeforeEach
  void setUp() {
    mapZoneCreateDTO = new MapZoneCreateDTO(
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
  @DisplayName("Test the constructor of MapZoneCreateDTO")
  void testConstructor() {
    assertEquals("Test Zone", mapZoneCreateDTO.getName());
    assertEquals("Description for Test Zone", mapZoneCreateDTO.getDescription());
    assertEquals("Address 1", mapZoneCreateDTO.getAddress());
    assertCoordinatesEqual(new CoordinatesDTO(63.424494, 10.439154), mapZoneCreateDTO.getCoordinates());
    assertEquals("Flood", mapZoneCreateDTO.getType());
    assertEquals(2, mapZoneCreateDTO.getSeverityLevel());

    List<List<List<CoordinatesDTO>>> expectedPolygon = List.of(List.of(
        List.of(
            new CoordinatesDTO(63.424494, 10.439154),
            new CoordinatesDTO(63.424694, 10.448154),
            new CoordinatesDTO(63.404494, 10.449154),
            new CoordinatesDTO(63.394494, 10.439154)
        )
    ));

    assertPolygonCoordinatesEqual(expectedPolygon, mapZoneCreateDTO.getPolygonCoordinates());
  }

  @Test
  @DisplayName("Test the getters of MapZoneCreateDTO")
  void testGetters() {
    assertEquals("Test Zone", mapZoneCreateDTO.getName());
    assertEquals("Description for Test Zone", mapZoneCreateDTO.getDescription());
    assertEquals("Address 1", mapZoneCreateDTO.getAddress());
    assertCoordinatesEqual(new CoordinatesDTO(63.424494, 10.439154), mapZoneCreateDTO.getCoordinates());
    assertEquals("Flood", mapZoneCreateDTO.getType());
    assertEquals(2, mapZoneCreateDTO.getSeverityLevel());

    List<List<List<CoordinatesDTO>>> expectedPolygon = List.of(List.of(
        List.of(
            new CoordinatesDTO(63.424494, 10.439154),
            new CoordinatesDTO(63.424694, 10.448154),
            new CoordinatesDTO(63.404494, 10.449154),
            new CoordinatesDTO(63.394494, 10.439154)
        )
    ));

    assertPolygonCoordinatesEqual(expectedPolygon, mapZoneCreateDTO.getPolygonCoordinates());
  }

  @Test
  @DisplayName("Test the setters of MapZoneCreateDTO")
  void testSetters() {
    mapZoneCreateDTO.setName("Updated Zone");
    mapZoneCreateDTO.setDescription("Updated description");
    mapZoneCreateDTO.setAddress("Updated address");
    mapZoneCreateDTO.setCoordinates(new CoordinatesDTO(63.424694, 10.448154));
    mapZoneCreateDTO.setType("Fire");
    mapZoneCreateDTO.setSeverityLevel(3);

    List<List<List<CoordinatesDTO>>> updatedPolygon = List.of(List.of(
        List.of(
            new CoordinatesDTO(63.424694, 10.448154),
            new CoordinatesDTO(63.424694, 10.448154),
            new CoordinatesDTO(63.404494, 10.449154),
            new CoordinatesDTO(63.394494, 10.439154)
        )
    ));

    mapZoneCreateDTO.setPolygonCoordinates(updatedPolygon);

    assertEquals("Updated Zone", mapZoneCreateDTO.getName());
    assertEquals("Updated description", mapZoneCreateDTO.getDescription());
    assertEquals("Updated address", mapZoneCreateDTO.getAddress());
    assertCoordinatesEqual(new CoordinatesDTO(63.424694, 10.448154), mapZoneCreateDTO.getCoordinates());
    assertEquals("Fire", mapZoneCreateDTO.getType());
    assertEquals(3, mapZoneCreateDTO.getSeverityLevel());

    assertPolygonCoordinatesEqual(updatedPolygon, mapZoneCreateDTO.getPolygonCoordinates());
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
