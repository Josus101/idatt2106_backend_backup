package org.ntnu.idatt2106.backend.controller;

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
import org.ntnu.idatt2106.backend.service.MapZonesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MapZonesControllerTest {

  @InjectMocks
  private MapZonesController mapZonesController;

  @Mock
  private MapZonesService mapZonesService;

  private List<MapZoneFullDTO> mockedMapZones;
  private MapZoneFullDTO zone1;
  private MapZoneFullDTO zone2;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    List<List<CoordinatesDTO>> mockedPolygon = List.of(
        List.of(
            new CoordinatesDTO(63.424494, 10.439154),
            new CoordinatesDTO(63.424694, 10.448154),
            new CoordinatesDTO(63.404494, 10.449154),
            new CoordinatesDTO(63.394494, 10.439154)
        )
    );

    zone1 = new MapZoneFullDTO(
        1L,
        "Test Zone 1",
        "Description for Zone 1",
        "Address 1",
        new CoordinatesDTO(63.424494, 10.439154),
        "Flood",
        2,
        List.of(mockedPolygon)
    );

    zone2 = new MapZoneFullDTO(
        2L,
        "Test Zone 2",
        "Description for Zone 2",
        "Address 2",
        new CoordinatesDTO(63.424693, 10.448153),
        "Fire",
        3,
        List.of(mockedPolygon)
    );

    mockedMapZones = List.of(zone1, zone2);
  }

  @Test
  @DisplayName("getAllEmergencyZones returns success with existing zones")
  void testGetAllEmergencyZones() {
    when(mapZonesService.getAllEmergencyZones()).thenReturn(mockedMapZones);

    ResponseEntity<?> response = mapZonesController.getEmergencyZones();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(mockedMapZones, response.getBody());
    verify(mapZonesService).getAllEmergencyZones();
  }

  @Test
  @DisplayName("getAllEmergencyZones returns not found when no zones exist")
  void testGetAllEmergencyZonesNotFound() {
    when(mapZonesService.getAllEmergencyZones()).thenReturn(List.of());

    ResponseEntity<?> response = mapZonesController.getEmergencyZones();

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error: No emergency zones found.", response.getBody());
  }

  @Test
  @DisplayName("getZonesInMapArea returns success with existing zones")
  void testGetZonesInMapArea() {
    List<CoordinatesDTO> mapArea = List.of(
        new CoordinatesDTO(63.424494, 10.439154),
        new CoordinatesDTO(63.424694, 10.448154),
        new CoordinatesDTO(63.404494, 10.449154),
        new CoordinatesDTO(63.394494, 10.439154)
    );

    when(mapZonesService.getEmergencyZonesInMapArea(mapArea, new Long[]{3L})).thenReturn(mockedMapZones);

    ResponseEntity<?> response = mapZonesController.getZonesInMapArea(mapArea, new Long[]{3L});

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(mockedMapZones, response.getBody());
  }

  @Test
  @DisplayName("getZonesInMapArea returns not found when no zones exist")
  void testGetZonesInMapAreaNotFound() {
    List<CoordinatesDTO> mapArea = List.of(
        new CoordinatesDTO(63.424494, 10.439154),
        new CoordinatesDTO(63.424694, 10.448154),
        new CoordinatesDTO(63.404494, 10.449154),
        new CoordinatesDTO(63.394494, 10.439154)
    );

    when(mapZonesService.getEmergencyZonesInMapArea(mapArea, new Long[]{3L})).thenReturn(List.of());

    ResponseEntity<?> response = mapZonesController.getZonesInMapArea(mapArea, new Long[]{3L});

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error: No emergency zones found in the specified area.", response.getBody());
  }

  @Test
  @DisplayName("getZonesInMapArea returns error when no map area is provided")
  void testGetZonesInMapAreaNoMapArea() {
    List<CoordinatesDTO> mapArea = null;

    when(mapZonesService.getEmergencyZonesInMapArea(mapArea, new Long[]{3L}))
        .thenThrow(new IllegalArgumentException("Map area cannot be null or empty."));

    ResponseEntity<?> response = mapZonesController.getZonesInMapArea(mapArea, new Long[]{3L});

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Map area cannot be null or empty.", response.getBody());
  }

  @Test
  @DisplayName("getZonesInMapArea returns bad request for invalid input")
  void testGetZonesInMapAreaBadRequest() {
    List<CoordinatesDTO> mapArea = List.of(
        new CoordinatesDTO(63.424494, 10.439154),
        new CoordinatesDTO(63.424694, 10.448154),
        new CoordinatesDTO(63.404494, 10.449154),
        new CoordinatesDTO(63.394494, 10.439154)
    );

    when(mapZonesService.getEmergencyZonesInMapArea(mapArea, new Long[]{3L}))
        .thenThrow(new IllegalArgumentException("Invalid input"));

    ResponseEntity<?> response = mapZonesController.getZonesInMapArea(mapArea, new Long[]{3L});

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Invalid input", response.getBody());
  }

  @Test
  @DisplayName("getZonesInMapArea returns internal server error")
  void testGetZonesInMapAreaInternalServerError() {
    List<CoordinatesDTO> mapArea = List.of(
        new CoordinatesDTO(63.424494, 10.439154),
        new CoordinatesDTO(63.424694, 10.448154),
        new CoordinatesDTO(63.404494, 10.449154),
        new CoordinatesDTO(63.394494, 10.439154)
    );

    when(mapZonesService.getEmergencyZonesInMapArea(mapArea, new Long[]{3L}))
        .thenThrow(new RuntimeException("Unexpected error"));

    ResponseEntity<?> response = mapZonesController.getZonesInMapArea(mapArea, new Long[]{3L});

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Error: An unexpected error occurred. Unexpected error", response.getBody());
  }

  @Test
  @DisplayName("getZoneById returns success for existing zone")
  void testGetZoneByIdSuccess() {
    when(mapZonesService.getEmergencyZoneById(1L)).thenReturn(zone1);

    ResponseEntity<?> response = mapZonesController.getZoneById(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(zone1, response.getBody());
  }

  @Test
  @DisplayName("getZoneById returns not found for non-existing zone")
  void testGetZoneByIdNotFound() {
    when(mapZonesService.getEmergencyZoneById(1L)).thenThrow(new RuntimeException("Zone not found"));

    ResponseEntity<?> response = mapZonesController.getZoneById(1L);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error: Emergency zone not found.Zone not found", response.getBody());
  }

  @Test
  @DisplayName("getZoneDescription returns success for existing zone")
  void testGetZoneDescriptionSuccess() {
    MapZoneDescDTO description = new MapZoneDescDTO(
        "Test Zone 1", "Description for Zone 1", "Address 1");
    when(mapZonesService.getEmergencyZoneDescById(1L)).thenReturn(description);

    ResponseEntity<?> response = mapZonesController.getZoneDescription(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(description, response.getBody());
  }

  @Test
  @DisplayName("getZoneDescription returns not found for non-existing zone")
  void testGetZoneDescriptionNotFound() {
    when(mapZonesService.getEmergencyZoneDescById(1L)).thenThrow(new RuntimeException("Zone not found"));

    ResponseEntity<?> response = mapZonesController.getZoneDescription(1L);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error: Emergency zone not found.Zone not found", response.getBody());
  }

  @Test
  @DisplayName("createZone returns success for valid input")
  void testCreateZoneSuccess() {
    MapZoneCreateDTO newZone = new MapZoneCreateDTO(
        "New Zone",
        "Description",
        "Address",
        new CoordinatesDTO(63.0, 10.0),
        "Flood",
        1,
        List.of()
    );
    when(mapZonesService.createZone(newZone)).thenReturn(1L);

    ResponseEntity<?> response = mapZonesController.createZone(newZone);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(1L, response.getBody());
  }

  @Test
  @DisplayName("createZone returns bad request for invalid input")
  void testCreateZoneBadRequest() {
    MapZoneCreateDTO newZone = new MapZoneCreateDTO();
    when(mapZonesService.createZone(newZone)).thenThrow(new RuntimeException("Invalid input"));

    ResponseEntity<?> response = mapZonesController.createZone(newZone);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Invalid input", response.getBody());
  }

  @Test
  @DisplayName("updateZone returns success for valid input")
  void testUpdateZoneSuccess() {
    MapZoneCreateDTO updatedZone = new MapZoneCreateDTO(
        "Updated Zone",
        "Updated Description",
        "Updated Address",
        new CoordinatesDTO(63.0, 10.0),
        "Fire",
        2,
        List.of()
    );

    ResponseEntity<?> response = mapZonesController.updateZone(1L, updatedZone);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Zone updated successfully.", response.getBody());
    verify(mapZonesService).updateZone(1L, updatedZone);
  }

  @Test
  @DisplayName("updateZone returns bad request for invalid input")
  void testUpdateZoneBadRequest() {
    MapZoneCreateDTO updatedZone = new MapZoneCreateDTO();
    doThrow(new RuntimeException("Invalid input")).when(mapZonesService).updateZone(1L, updatedZone);

    ResponseEntity<?> response = mapZonesController.updateZone(1L, updatedZone);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Invalid input", response.getBody());
  }

  @Test
  @DisplayName("deleteZone returns success for valid input")
  void testDeleteZoneSuccess() {
    ResponseEntity<?> response = mapZonesController.deleteZone(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Zone deleted successfully.", response.getBody());
    verify(mapZonesService).deleteZone(1L);
  }

  @Test
  @DisplayName("deleteZone returns bad request for invalid input")
  void testDeleteZoneBadRequest() {
    doThrow(new RuntimeException("Invalid input")).when(mapZonesService).deleteZone(1L);

    ResponseEntity<?> response = mapZonesController.deleteZone(1L);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Invalid input", response.getBody());
  }
}