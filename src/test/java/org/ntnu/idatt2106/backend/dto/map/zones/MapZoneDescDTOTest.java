package org.ntnu.idatt2106.backend.dto.map.zones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the MapZoneDescDTO class.
 */
public class MapZoneDescDTOTest {

  private MapZoneDescDTO mapZoneDescDTO;

  @BeforeEach
  void setUp() {
    mapZoneDescDTO = new MapZoneDescDTO(
        "Test Zone",
        "Description for Test Zone",
        "Address 1"
    );
  }

  @Test
  void testConstructor() {
    assertEquals("Test Zone", mapZoneDescDTO.getName());
    assertEquals("Description for Test Zone", mapZoneDescDTO.getDescription());
    assertEquals("Address 1", mapZoneDescDTO.getAddress());
  }

  @Test
  void testGetters() {
    assertEquals("Test Zone", mapZoneDescDTO.getName());
    assertEquals("Description for Test Zone", mapZoneDescDTO.getDescription());
    assertEquals("Address 1", mapZoneDescDTO.getAddress());
  }

  @Test
  void testSetters() {
    mapZoneDescDTO.setName("Updated Zone");
    mapZoneDescDTO.setDescription("Updated Description for Test Zone");
    mapZoneDescDTO.setAddress("Updated Address for Test Zone");

    assertEquals("Updated Zone", mapZoneDescDTO.getName());
    assertEquals("Updated Description for Test Zone", mapZoneDescDTO.getDescription());
    assertEquals("Updated Address for Test Zone", mapZoneDescDTO.getAddress());
  }
}
