package org.ntnu.idatt2106.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ntnu.idatt2106.backend.dto.map.CoordinatesDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.MapZoneCreateDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.MapZoneDescDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.MapZoneFullDTO;
import org.ntnu.idatt2106.backend.model.map.Coordinate;
import org.ntnu.idatt2106.backend.model.map.CoordinatePolygon;
import org.ntnu.idatt2106.backend.model.map.CoordinateRing;
import org.ntnu.idatt2106.backend.model.map.MapZone;
import org.ntnu.idatt2106.backend.repo.map.MapZoneRepo;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MapZonesServiceTest {

  @InjectMocks
  private MapZonesService mapZonesService;

  @Mock
  private MapZoneRepo mapZoneRepo;

  private MapZone mapZone;
  private MapZoneCreateDTO mapZoneCreateDTO;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    Coordinate coordinate = new Coordinate(63.424494, 10.439154);
    CoordinateRing outerRing = new CoordinateRing(List.of(coordinate));
    CoordinatePolygon polygon = new CoordinatePolygon(outerRing, List.of());
    mapZone = new MapZone("Test Zone", "Description", "Address", coordinate, "Flood", 2);
    mapZone.setId(1L);
    mapZone.setPolygons(List.of(polygon));

    mapZoneCreateDTO = new MapZoneCreateDTO(
      "New Zone",
      "New Description",
      "New Address",
      new CoordinatesDTO(63.0, 10.0),
      "Fire",
      3,
      List.of(
        // First polygon
        List.of(
          // Outer ring
          List.of(
            new CoordinatesDTO(63.0, 10.0),
            new CoordinatesDTO(63.0, 11.0),
            new CoordinatesDTO(64.0, 11.0),
            new CoordinatesDTO(64.0, 10.0),
            new CoordinatesDTO(63.0, 10.0)
          ),
          // Inner ring
          List.of(
            new CoordinatesDTO(63.2, 10.2),
            new CoordinatesDTO(63.2, 10.8),
            new CoordinatesDTO(63.8, 10.8),
            new CoordinatesDTO(63.8, 10.2),
            new CoordinatesDTO(63.2, 10.2)
          )
        ),
        // Second polygon
        List.of(
          // Outer ring
          List.of(
            new CoordinatesDTO(63.5, 10.5),
            new CoordinatesDTO(63.5, 11.5),
            new CoordinatesDTO(64.5, 11.5),
            new CoordinatesDTO(64.5, 10.5),
            new CoordinatesDTO(63.5, 10.5)
          )
        )
      )
    );
  }

  @Test
  @DisplayName("Should return all emergency zones")
  void testGetAllEmergencyZones() {
    when(mapZoneRepo.findAll()).thenReturn(List.of(mapZone));

    List<MapZoneFullDTO> result = mapZonesService.getAllEmergencyZones();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Test Zone", result.getFirst().getName());
    verify(mapZoneRepo, times(1)).findAll();
  }

  @Nested
  class GetEmergencyZoneByIdTests {

    @Test
    @DisplayName("Should return zone by ID")
    void testGetEmergencyZoneById() {
      when(mapZoneRepo.findById(1L)).thenReturn(Optional.of(mapZone));

      MapZoneFullDTO result = mapZonesService.getEmergencyZoneById(1L);

      assertNotNull(result);
      assertEquals("Test Zone", result.getName());
      verify(mapZoneRepo, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return null when zone not found")
    void testGetEmergencyZoneByIdNotFound() {
      when(mapZoneRepo.findById(1L)).thenReturn(Optional.empty());

      MapZoneFullDTO result = mapZonesService.getEmergencyZoneById(1L);

      assertNull(result);
      verify(mapZoneRepo, times(1)).findById(1L);
    }
  }

  @Nested
  class GetEmergencyZoneDescByIdTests {

    @Test
    @DisplayName("Should return zone description by ID")
    void testGetEmergencyZoneDescById() {
      when(mapZoneRepo.findById(1L)).thenReturn(Optional.of(mapZone));

      MapZoneDescDTO result = mapZonesService.getEmergencyZoneDescById(1L);

      assertNotNull(result);
      assertEquals("Test Zone", result.getName());
      verify(mapZoneRepo, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return null when zone not found for description")
    void testGetEmergencyZoneDescByIdNotFound() {
      when(mapZoneRepo.findById(1L)).thenReturn(Optional.empty());

      MapZoneDescDTO result = mapZonesService.getEmergencyZoneDescById(1L);

      assertNull(result);
      verify(mapZoneRepo, times(1)).findById(1L);
    }
  }

  @Test
  @DisplayName("Should create and return ID of new zone")
  void testCreateZone() {
    when(mapZoneRepo.save(any(MapZone.class))).thenReturn(mapZone);

    Long result = mapZonesService.createZone(mapZoneCreateDTO);

    assertNotNull(result);
    assertEquals(1L, result);
    verify(mapZoneRepo, times(1)).save(any(MapZone.class));
  }

  @Nested
  class GetEmergencyZonesInMapAreaTests {

    private List<CoordinatesDTO> area;

    @BeforeEach
    void setupArea() {
      CoordinatesDTO topLeft = new CoordinatesDTO(64.0, 9.0);
      CoordinatesDTO bottomRight = new CoordinatesDTO(62.0, 11.0);
      area = List.of(topLeft, bottomRight);
    }

    @Test
    @DisplayName("Should return zones within bounding box excluding specific IDs")
    void testZonesWithinArea() {
      MapZone zoneInside1 = getZoneInside1();
      MapZone zoneInside2 = getZoneInside2();

      MapZone zoneExcluded = getZoneExcluded();

      // Zone outside the bounding box
      MapZone zoneOutside = new MapZone("Outside", "Desc4", "Addr4", new Coordinate(70.0, 20.0), "Type", 1);
      zoneOutside.setId(4L);

      when(mapZoneRepo.findAll()).thenReturn(List.of(zoneInside1, zoneInside2, zoneExcluded, zoneOutside));

      // Call the method with the bounding box and excluded ID
      List<MapZoneFullDTO> result = mapZonesService.getEmergencyZonesInMapArea(area, new Long[]{3L});

      // Assertions
      assertNotNull(result);
      assertEquals(2, result.size()); // Only zoneInside1 and zoneInside2 should be returned
      assertTrue(result.stream().anyMatch(zone -> zone.getId().equals(1L)));
      assertTrue(result.stream().anyMatch(zone -> zone.getId().equals(2L)));
      assertFalse(result.stream().anyMatch(zone -> zone.getId().equals(3L))); // Excluded zone
      assertFalse(result.stream().anyMatch(zone -> zone.getId().equals(4L))); // Outside zone

      verify(mapZoneRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no zones are in bounding box")
    void testNoZonesInArea() {
      MapZone zoneOutside1 = new MapZone("Outside1", "Desc", "Addr", new Coordinate(70.0, 20.0), "Type", 1);
      zoneOutside1.setId(4L);
      MapZone zoneOutside2 = new MapZone("Outside2", "Desc", "Addr", new Coordinate(10.0, 5.0), "Type", 1);
      zoneOutside2.setId(5L);

      when(mapZoneRepo.findAll()).thenReturn(List.of(zoneOutside1, zoneOutside2));

      List<MapZoneFullDTO> result = mapZonesService.getEmergencyZonesInMapArea(area, new Long[]{});

      assertNotNull(result);
      assertTrue(result.isEmpty());
      verify(mapZoneRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should throw when bounding box is empty")
    void testThrowsOnEmptyCoordinates() {
      assertThrows(NumberFormatException.class,
          () -> mapZonesService.getEmergencyZonesInMapArea(List.of(), new Long[]{}));
    }
  }

  private static MapZone getZoneExcluded() {
    CoordinateRing outerRing3 = new CoordinateRing(List.of(
        new Coordinate(63.5, 10.5),
        new Coordinate(63.5, 11.0),
        new Coordinate(64.0, 11.0),
        new Coordinate(64.0, 10.5),
        new Coordinate(63.5, 10.5)
    ));
    CoordinatePolygon polygon3 = new CoordinatePolygon(outerRing3, List.of());
    MapZone zoneExcluded = new MapZone("Excluded", "Desc3", "Addr3", new Coordinate(63.5, 10.5), "Type", 1);
    zoneExcluded.setId(3L);
    zoneExcluded.setPolygons(List.of(polygon3));
    return zoneExcluded;
  }

  @Nested
  class UpdateZoneTests {

    @Test
    @DisplayName("Should update an existing zone")
    void testUpdateZone() {
      when(mapZoneRepo.findById(1L)).thenReturn(Optional.of(mapZone));

      assertDoesNotThrow(() -> mapZonesService.updateZone(1L, mapZoneCreateDTO));

      verify(mapZoneRepo, times(1)).findById(1L);
      verify(mapZoneRepo, times(1)).save(any(MapZone.class));
    }

    @Test
    @DisplayName("Should throw when zone to update not found")
    void testUpdateZoneNotFound() {
      when(mapZoneRepo.findById(1L)).thenReturn(Optional.empty());

      Exception exception = assertThrows(IllegalArgumentException.class,
          () -> mapZonesService.updateZone(1L, mapZoneCreateDTO));

      assertEquals("Zone (1) not found", exception.getMessage());
      verify(mapZoneRepo, times(1)).findById(1L);
    }
  }

  @Nested
  class DeleteZoneTests {

    @Test
    @DisplayName("Should delete an existing zone")
    void testDeleteZone() {
      when(mapZoneRepo.findById(1L)).thenReturn(Optional.of(mapZone));

      assertDoesNotThrow(() -> mapZonesService.deleteZone(1L));

      verify(mapZoneRepo, times(1)).findById(1L);
      verify(mapZoneRepo, times(1)).delete(mapZone);
    }

    @Test
    @DisplayName("Should throw when zone to delete not found")
    void testDeleteZoneNotFound() {
      when(mapZoneRepo.findById(1L)).thenReturn(Optional.empty());

      Exception exception = assertThrows(IllegalArgumentException.class,
          () -> mapZonesService.deleteZone(1L));

      assertEquals("Zone (1) not found", exception.getMessage());
      verify(mapZoneRepo, times(1)).findById(1L);
    }
  }

  @Test
  @DisplayName("Should remove orphan polygons on update")
  void testOrphanRemoval() {
    Coordinate coordinate = new Coordinate(63.424494, 10.439154);
    CoordinateRing outerRing = new CoordinateRing(List.of(coordinate));
    CoordinatePolygon oldPolygon = new CoordinatePolygon(outerRing, List.of());
    mapZone.setPolygons(List.of(oldPolygon));

    when(mapZoneRepo.findById(1L)).thenReturn(Optional.of(mapZone));

    mapZoneCreateDTO.setPolygonCoordinates(List.of(
        List.of(List.of(new CoordinatesDTO(63.0, 10.0)))
    ));

    mapZonesService.updateZone(1L, mapZoneCreateDTO);

    verify(mapZoneRepo, times(1)).save(mapZone);
    assertTrue(mapZone.getPolygons().stream()
        .noneMatch(polygon -> polygon.equals(oldPolygon)));
  }


  private static MapZone getZoneInside2() {
    CoordinateRing outerRing2 = new CoordinateRing(List.of(
        new Coordinate(63.2, 10.2),
        new Coordinate(63.2, 10.8),
        new Coordinate(63.8, 10.8),
        new Coordinate(63.8, 10.2),
        new Coordinate(63.2, 10.2)
    ));
    CoordinatePolygon polygon2 = new CoordinatePolygon(outerRing2, List.of());
    MapZone zoneInside2 = new MapZone("Inside2", "Desc2", "Addr2", new Coordinate(63.2, 10.2), "Type", 1);
    zoneInside2.setId(2L);
    zoneInside2.setPolygons(List.of(polygon2));
    return zoneInside2;
  }

  private static MapZone getZoneInside1() {
    CoordinateRing outerRing1 = new CoordinateRing(List.of(
        new Coordinate(63.0, 10.0),
        new Coordinate(63.0, 10.5),
        new Coordinate(63.5, 10.5),
        new Coordinate(63.5, 10.0),
        new Coordinate(63.0, 10.0)
    ));
    CoordinatePolygon polygon1 = new CoordinatePolygon(outerRing1, List.of());
    MapZone zoneInside1 = new MapZone("Inside1", "Desc1", "Addr1", new Coordinate(63.0, 10.0), "Type", 1);
    zoneInside1.setId(1L);
    zoneInside1.setPolygons(List.of(polygon1));
    return zoneInside1;
  }
}
