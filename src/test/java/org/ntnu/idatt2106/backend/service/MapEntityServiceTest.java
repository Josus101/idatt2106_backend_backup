package org.ntnu.idatt2106.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ntnu.idatt2106.backend.dto.map.CoordinatesDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.ZoneCreateDTO;
import org.ntnu.idatt2106.backend.model.map.*;
import org.ntnu.idatt2106.backend.repo.map.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MapEntityServiceTest {

  @InjectMocks
  private MapEntityService mapEntityService;

  @Mock
  private MapEntityRepo mapEntityRepo;

  @Mock
  private MapEntityTypeRepo mapEntityTypeRepo;

  @Mock
  private MapZoneTypeRepo mapZoneTypeRepo;

  @Mock
  private MapMarkerTypeRepo mapMarkerTypeRepo;

  private MapEntity testZone;
  private MapEntityType zoneType;
  private MapZoneType mapZoneType;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    zoneType = new MapEntityType();
    zoneType.setName("zone");

    mapZoneType = new MapZoneType();
    mapZoneType.setName("TestType");

    Coordinate coordinate = new Coordinate(10.0, 20.0);

    testZone = new MapEntity();
    testZone.setId(1L);
    testZone.setName("Zone A");
    testZone.setDescription("Description");
    testZone.setAddress("Address");
    testZone.setSeverityLevel(2);
    testZone.setMapEntityType(zoneType);
    testZone.setMapZoneType(mapZoneType);
    testZone.setCoordinatePoint(coordinate);
  }

  @Test
  @DisplayName("getMapZoneById should throw when zone does not exist")
  void getMapZoneByIdNotFound() {
    when(mapEntityRepo.findById(1L)).thenReturn(null);

    Exception exception = assertThrows(Exception.class, () -> mapEntityService.getMapZoneById(1L));
    assertTrue(exception.getMessage().contains("not found"));
  }

  @Test
  @DisplayName("createZone should return ID when created successfully")
  void createZoneSuccess() {
    ZoneCreateDTO dto = new ZoneCreateDTO();
    dto.setName("New Zone");
    dto.setDescription("desc");
    dto.setAddress("addr");
    dto.setSeverityLevel(1);
    dto.setType("TestType");
    dto.setCoordinates(new CoordinatesDTO(1.0, 2.0));
    dto.setPolygonCoordinateList("");

    when(mapEntityTypeRepo.findByName("zone")).thenReturn(Optional.of(zoneType));
    when(mapZoneTypeRepo.findByName("TestType")).thenReturn(Optional.of(mapZoneType));

    MapEntity saved = new MapEntity();
    saved.setId(42L);

    when(mapEntityRepo.save(any(MapEntity.class))).thenReturn(saved);

    Long id = mapEntityService.createZone(dto);

    assertNotNull(id);
    assertEquals(42L, id);
  }

  @Test
  @DisplayName("getAllMapZones should return list of zones")
  void getAllMapZonesSuccess() {
    when(mapEntityRepo.findAllByMapEntityType_Name("zone")).thenReturn(List.of(testZone));

    var result = mapEntityService.getAllMapZones();

    assertEquals(1, result.size());
    assertEquals("Zone A", result.getFirst().getName());
  }

  @Test
  @DisplayName("getMapEntityCoordinates should throw when not found")
  void getMapEntityCoordinatesNotFound() {
    when(mapEntityRepo.findById(1L)).thenReturn(null);

    Exception ex = assertThrows(IllegalArgumentException.class,
        () -> mapEntityService.getMapEntityCoordinates(1L));

    assertTrue(ex.getMessage().contains("not found"));
  }
}
