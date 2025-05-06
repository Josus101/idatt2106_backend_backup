package org.ntnu.idatt2106.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
        List.of(List.of(List.of(new CoordinatesDTO(63.0, 10.0))))
    );
  }

  @Test
  @DisplayName("Test getAllEmergencyZones returns all zones")
  void testGetAllEmergencyZones() {
    when(mapZoneRepo.findAll()).thenReturn(List.of(mapZone));

    List<MapZoneFullDTO> result = mapZonesService.getAllEmergencyZones();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Test Zone", result.getFirst().getName());
    verify(mapZoneRepo, times(1)).findAll();
  }

  @Test
  @DisplayName("Test getEmergencyZoneById returns a zone")
  void testGetEmergencyZoneById() {
    when(mapZoneRepo.findById(1L)).thenReturn(Optional.of(mapZone));

    MapZoneFullDTO result = mapZonesService.getEmergencyZoneById(1L);

    assertNotNull(result);
    assertEquals("Test Zone", result.getName());
    verify(mapZoneRepo, times(1)).findById(1L);
  }

  @Test
  @DisplayName("Test getEmergencyZoneById returns null for non-existing zone")
  void testGetEmergencyZoneByIdNotFound() {
    when(mapZoneRepo.findById(1L)).thenReturn(Optional.empty());

    MapZoneFullDTO result = mapZonesService.getEmergencyZoneById(1L);

    assertNull(result);
    verify(mapZoneRepo, times(1)).findById(1L);
  }

  @Test
  @DisplayName("Test getEmergencyZoneDescById returns a description")
  void testGetEmergencyZoneDescById() {
    when(mapZoneRepo.findById(1L)).thenReturn(Optional.of(mapZone));

    MapZoneDescDTO result = mapZonesService.getEmergencyZoneDescById(1L);

    assertNotNull(result);
    assertEquals("Test Zone", result.getName());
    verify(mapZoneRepo, times(1)).findById(1L);
  }

  @Test
  @DisplayName("Test createZone saves a new zone")
  void testCreateZone() {
    when(mapZoneRepo.save(any(MapZone.class))).thenReturn(mapZone);

    Long result = mapZonesService.createZone(mapZoneCreateDTO);

    assertNotNull(result);
    assertEquals(1L, result);
    verify(mapZoneRepo, times(1)).save(any(MapZone.class));
  }

  @Test
  @DisplayName("Test updateZone updates an existing zone")
  void testUpdateZone() {
    when(mapZoneRepo.findById(1L)).thenReturn(Optional.of(mapZone));

    assertDoesNotThrow(() -> mapZonesService.updateZone(1L, mapZoneCreateDTO));

    verify(mapZoneRepo, times(1)).findById(1L);
    verify(mapZoneRepo, times(1)).save(any(MapZone.class));
  }

  @Test
  @DisplayName("Test updateZone throws exception for non-existing zone")
  void testUpdateZoneNotFound() {
    when(mapZoneRepo.findById(1L)).thenReturn(Optional.empty());

    Exception exception = assertThrows(IllegalArgumentException.class, () -> mapZonesService.updateZone(1L, mapZoneCreateDTO));

    assertEquals("Zone (1) not found", exception.getMessage());
    verify(mapZoneRepo, times(1)).findById(1L);
  }

  @Test
  @DisplayName("Test deleteZone deletes an existing zone")
  void testDeleteZone() {
    when(mapZoneRepo.findById(1L)).thenReturn(Optional.of(mapZone));

    assertDoesNotThrow(() -> mapZonesService.deleteZone(1L));

    verify(mapZoneRepo, times(1)).findById(1L);
    verify(mapZoneRepo, times(1)).delete(mapZone);
  }

  @Test
  @DisplayName("Test deleteZone throws exception for non-existing zone")
  void testDeleteZoneNotFound() {
    when(mapZoneRepo.findById(1L)).thenReturn(Optional.empty());

    Exception exception = assertThrows(IllegalArgumentException.class, () -> mapZonesService.deleteZone(1L));

    assertEquals("Zone (1) not found", exception.getMessage());
    verify(mapZoneRepo, times(1)).findById(1L);
  }

  @Test
  @DisplayName("Test orphan removal for polygons")
  void testOrphanRemoval() {
    // Mock the existing zone with polygons
    Coordinate coordinate = new Coordinate(63.424494, 10.439154);
    CoordinateRing outerRing = new CoordinateRing(List.of(coordinate));
    CoordinatePolygon oldPolygon = new CoordinatePolygon(outerRing, List.of());
    mapZone.setPolygons(List.of(oldPolygon));

    when(mapZoneRepo.findById(1L)).thenReturn(Optional.of(mapZone));

    // Update the zone with new polygons
    mapZoneCreateDTO.setPolygonCoordinates(List.of(
        List.of(List.of(new CoordinatesDTO(63.0, 10.0)))
    ));

    mapZonesService.updateZone(1L, mapZoneCreateDTO);

    // Verify the old polygon is removed
    verify(mapZoneRepo, times(1)).save(mapZone);
    assertTrue(mapZone.getPolygons().stream()
        .noneMatch(polygon -> polygon.equals(oldPolygon)));
  }
}