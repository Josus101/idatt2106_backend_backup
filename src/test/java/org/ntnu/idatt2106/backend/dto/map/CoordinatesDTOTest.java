package org.ntnu.idatt2106.backend.dto.map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the CoordinatesDTO class.
 */
class CoordinatesDTOTest {

  private CoordinatesDTO coordinatesDTO;

  @BeforeEach
  void setUp() {
    coordinatesDTO = new CoordinatesDTO(10.0, 20.0);
  }

  @Test
  @DisplayName("Test the constructor of CoordinatesDTO")
  void testConstructor() {
    assertNotNull(coordinatesDTO);

    assertEquals(10.0, coordinatesDTO.getLatitude());
    assertEquals(20.0, coordinatesDTO.getLongitude());
  }

  @Test
  @DisplayName("Test the getters of CoordinatesDTO")
  void testGetters() {
    assertEquals(10.0, coordinatesDTO.getLatitude());
    assertEquals(20.0, coordinatesDTO.getLongitude());
  }

  @Test
  @DisplayName("Test the setters of CoordinatesDTO")
  void testSetters() {
    coordinatesDTO.setLatitude(30.0);
    coordinatesDTO.setLongitude(40.0);

    assertEquals(30.0, coordinatesDTO.getLatitude());
    assertEquals(40.0, coordinatesDTO.getLongitude());
  }
}