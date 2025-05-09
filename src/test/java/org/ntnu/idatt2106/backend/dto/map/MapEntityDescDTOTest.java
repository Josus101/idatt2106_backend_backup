package org.ntnu.idatt2106.backend.dto.map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the MapEntityDescDTO class.
 */
public class MapEntityDescDTOTest {

  private MapEntityDescDTO mapEntityDescDTO;

  @BeforeEach
  void setUp() {
    mapEntityDescDTO = new MapEntityDescDTO(
        "Test Zone",
        "Description for Test Zone",
        "Address 1"
    );
  }

  @Test
  void testConstructor() {
    assertEquals("Test Zone", mapEntityDescDTO.getName());
    assertEquals("Description for Test Zone", mapEntityDescDTO.getDescription());
    assertEquals("Address 1", mapEntityDescDTO.getAddress());
  }

  @Test
  void testGetters() {
    assertEquals("Test Zone", mapEntityDescDTO.getName());
    assertEquals("Description for Test Zone", mapEntityDescDTO.getDescription());
    assertEquals("Address 1", mapEntityDescDTO.getAddress());
  }

  @Test
  void testSetters() {
    mapEntityDescDTO.setName("Updated Zone");
    mapEntityDescDTO.setDescription("Updated Description for Test Zone");
    mapEntityDescDTO.setAddress("Updated Address for Test Zone");

    assertEquals("Updated Zone", mapEntityDescDTO.getName());
    assertEquals("Updated Description for Test Zone", mapEntityDescDTO.getDescription());
    assertEquals("Updated Address for Test Zone", mapEntityDescDTO.getAddress());
  }
}
