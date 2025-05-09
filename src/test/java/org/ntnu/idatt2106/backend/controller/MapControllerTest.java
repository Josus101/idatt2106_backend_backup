package org.ntnu.idatt2106.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ntnu.idatt2106.backend.dto.map.CoordinatesDTO;
import org.ntnu.idatt2106.backend.dto.map.MapEntityDescDTO;
import org.ntnu.idatt2106.backend.dto.map.markers.MarkerFullDTO;
import org.ntnu.idatt2106.backend.dto.map.types.TypeFullDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.ZoneCreateDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.ZoneFullDTO;
import org.ntnu.idatt2106.backend.service.MapEntityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MapControllerTest {

  @InjectMocks
  private MapController mapController;

  @Mock
  private MapEntityService mapEntityService;

  private ZoneFullDTO testZone;
  private MarkerFullDTO testMarker;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    testZone = new ZoneFullDTO();
    testZone.setId(1L);
    testZone.setName("Zone A");

    testMarker = new MarkerFullDTO();
    testMarker.setId(100L);
    testMarker.setName("Marker A");
  }

  @Test
  @DisplayName("getEmergencyZones returns zones when found")
  void getEmergencyZonesSuccess() {
    when(mapEntityService.getAllMapZones()).thenReturn(List.of(testZone));

    ResponseEntity<?> response = mapController.getEmergencyZones();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertInstanceOf(List.class, response.getBody());
  }

  @Test
  @DisplayName("getZoneById returns a zone when found")
  void getZoneByIdSuccess() {
    when(mapEntityService.getMapZoneById(1L)).thenReturn(testZone);

    ResponseEntity<?> response = mapController.getZoneById(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(testZone, response.getBody());
  }

  @Test
  @DisplayName("createZone returns ID when successful")
  void createZoneSuccess() {
    ZoneCreateDTO newZone = new ZoneCreateDTO();
    when(mapEntityService.createZone(newZone)).thenReturn(2L);

    ResponseEntity<?> response = mapController.createZone(newZone);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(2L, response.getBody());
  }

  @Test
  @DisplayName("getMarkers returns list when found")
  void getMarkersSuccess() {
    when(mapEntityService.getAllMapMarkers()).thenReturn(List.of(testMarker));

    ResponseEntity<?> response = mapController.getMarkers();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertInstanceOf(List.class, response.getBody());
  }

  @Test
  @DisplayName("getMarkerById returns marker when found")
  void getMarkerByIdSuccess() {
    when(mapEntityService.getMapMarkerById(100L)).thenReturn(testMarker);

    ResponseEntity<?> response = mapController.getMarkerById(100L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(testMarker, response.getBody());
  }

  @Test
  @DisplayName("getAllZoneTypes returns types when found")
  void getAllZoneTypesSuccess() {
    TypeFullDTO type = new TypeFullDTO();
    type.setType("Evacuation");
    when(mapEntityService.getZoneTypes()).thenReturn(List.of(type));

    ResponseEntity<?> response = mapController.getAllZoneTypes();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody() instanceof List);
    assertEquals("Evacuation", ((List<TypeFullDTO>) response.getBody()).getFirst().getType());
  }

  @Test
  @DisplayName("getMapEntityCoordinates returns coordinates when found")
  void getCoordinatesByIdSuccess() {
    CoordinatesDTO coords = new CoordinatesDTO();
    coords.setLatitude(60.0);
    coords.setLongitude(5.0);
    when(mapEntityService.getMapEntityCoordinates(1L)).thenReturn(coords);

    ResponseEntity<?> response = mapController.getMapEntityCoordinates(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(coords, response.getBody());
  }

  @Test
  @DisplayName("getDescription returns description when found")
  void getDescriptionSuccess() {
    MapEntityDescDTO desc = new MapEntityDescDTO();
    desc.setDescription("Sample description");
    when(mapEntityService.getMapEntityDescById(1L)).thenReturn(desc);

    ResponseEntity<?> response = mapController.getDescription(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(desc, response.getBody());
  }
}
