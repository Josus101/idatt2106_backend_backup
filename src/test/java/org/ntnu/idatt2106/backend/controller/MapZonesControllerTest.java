package org.ntnu.idatt2106.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ntnu.idatt2106.backend.dto.map.CoordinatesDTO;
import org.ntnu.idatt2106.backend.dto.map.zones.EmergencyZoneFullDTO;
import org.ntnu.idatt2106.backend.service.MapZonesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the MapZonesController class.
 */
public class MapZonesControllerTest {

  @InjectMocks
  private MapZonesController mapZonesController;

  @Mock
  MapZonesService mapZonesService;

  private List<EmergencyZoneFullDTO> mockedMapZones;

  private List<List<CoordinatesDTO>> mockedPolygonJustOuterRing = List.of(
      List.of(
          new CoordinatesDTO(63.424494, 10.439154),
          new CoordinatesDTO(63.424694, 10.448154),
          new CoordinatesDTO(63.404494, 10.449154),
          new CoordinatesDTO(63.394494, 10.439154),
          new CoordinatesDTO(63.414494, 10.440154),
          new CoordinatesDTO(63.413494, 10.442154)
      )
  );
  private List<List<CoordinatesDTO>> mockedPolygonWithInnerRing = List.of(
      List.of(
          new CoordinatesDTO(63.424494, 10.439154),
          new CoordinatesDTO(63.424694, 10.448154),
          new CoordinatesDTO(63.404494, 10.449154),
          new CoordinatesDTO(63.394494, 10.439154),
          new CoordinatesDTO(63.414494, 10.440154),
          new CoordinatesDTO(63.413494, 10.442154)
      ),
      List.of(
          new CoordinatesDTO(63.414494, 10.440154),
          new CoordinatesDTO(63.414994, 10.444154),
          new CoordinatesDTO(63.410494, 10.444154),
          new CoordinatesDTO(63.410494, 10.440154)
      )
  );

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    MockMvcBuilders.standaloneSetup(mapZonesController).build();

    EmergencyZoneFullDTO zone1 = new EmergencyZoneFullDTO(
        1L,
        "Test Zone 1",
        "This is a description for test zone 1",
        "Address for zone 1",
        new CoordinatesDTO(63.424494, 10.439154),
        "Flood",
        2,
        List.of(mockedPolygonJustOuterRing)
    );
    EmergencyZoneFullDTO zone2 = new EmergencyZoneFullDTO(
        2L,
        "Test Zone 2",
        "This is a description for test zone 2",
        "Address for zone 2",
        new CoordinatesDTO(63.424693, 10.448153),
        "Fire",
        3,
        List.of(mockedPolygonWithInnerRing)
    );
    EmergencyZoneFullDTO zone3 = new EmergencyZoneFullDTO(
        3L,
        "Test Zone 3",
        "This is a description for test zone 3",
        "Address for zone 3",
        new CoordinatesDTO(63.404494, 10.449154),
        "Power Outage",
        1,
        List.of(mockedPolygonJustOuterRing, mockedPolygonWithInnerRing)
    );
    mockedMapZones = List.of(zone1, zone2, zone3);
  }

  @Test
  @DisplayName("getAllEmergencyZones method returns success on existing complete zones")
  void testGetAllEmergencyZones() throws Exception {
    when(mapZonesService.getAllEmergencyZones()).thenReturn(mockedMapZones);

    ResponseEntity<?> response = mapZonesController.getEmergencyZones();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(mockedMapZones, response.getBody());
    verify(mapZonesService).getAllEmergencyZones();
  }
}
