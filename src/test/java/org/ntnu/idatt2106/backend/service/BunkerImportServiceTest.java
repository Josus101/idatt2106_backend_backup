package org.ntnu.idatt2106.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.ntnu.idatt2106.backend.model.map.MapEntity;
import org.ntnu.idatt2106.backend.model.map.MapMarkerType;
import org.ntnu.idatt2106.backend.repo.map.MapEntityRepo;
import org.ntnu.idatt2106.backend.repo.map.MapEntityTypeRepo;
import org.ntnu.idatt2106.backend.repo.map.MapMarkerTypeRepo;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BunkerImportServiceTest {

    @InjectMocks
    private BunkerImportService bunkerImportService;

    @Mock
    private MapEntityRepo mapEntityRepo;

    @Mock
    private MapMarkerTypeRepo mapMarkerTypeRepo;

    @Mock
    private MapEntityTypeRepo mapEntityTypeRepo;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should import bunker data successfully")
    void testImportBunkerDataFromJsonSuccess() throws Exception {
        // Arrange
        JsonNode rootNode = mock(JsonNode.class);
        JsonNode featuresNode = mock(JsonNode.class);
        JsonNode featureNode = mock(JsonNode.class);
        JsonNode propertiesNode = mock(JsonNode.class);
        JsonNode geometryNode = mock(JsonNode.class);
        JsonNode coordinatesNode = mock(JsonNode.class);
        JsonNode lokalIdNode = mock(JsonNode.class);
        JsonNode addressNode = mock(JsonNode.class);
        JsonNode capacityNode = mock(JsonNode.class);
        JsonNode xCoordNode = mock(JsonNode.class);
        JsonNode yCoordNode = mock(JsonNode.class);

        // Set up mocks
        when(rootNode.get("features")).thenReturn(featuresNode);
        when(featuresNode.iterator()).thenReturn(List.of(featureNode).iterator());
        when(featureNode.get("properties")).thenReturn(propertiesNode);
        when(featureNode.get("geometry")).thenReturn(geometryNode);

        when(propertiesNode.get("lokalId")).thenReturn(lokalIdNode);
        when(propertiesNode.get("adresse")).thenReturn(addressNode);
        when(propertiesNode.get("plasser")).thenReturn(capacityNode);

        when(lokalIdNode.asText()).thenReturn("localID123");
        when(addressNode.asText()).thenReturn("Test Address");
        when(capacityNode.asText()).thenReturn("500");

        when(geometryNode.get("coordinates")).thenReturn(coordinatesNode);
        when(coordinatesNode.get(0)).thenReturn(xCoordNode);
        when(coordinatesNode.get(1)).thenReturn(yCoordNode);
        when(xCoordNode.asDouble()).thenReturn(500000.0);
        when(yCoordNode.asDouble()).thenReturn(7000000.0);

        // Mock mapMarkerTypeRepo
        MapMarkerType bunkerMapMarkerType = new MapMarkerType("Bunker");
        when(mapMarkerTypeRepo.findByName("Bunker")).thenReturn(Optional.of(bunkerMapMarkerType));

        // Mock repo to return empty so service will be saved
        when(mapEntityRepo.findByLocalID("localID123")).thenReturn(Optional.empty());

        // Spy BunkerImportService for mocking file reading
        BunkerImportService spyService = Mockito.spy(bunkerImportService);
        doReturn(rootNode).when(spyService).readJsonFromFile(anyString());

        // Act
        spyService.importBunkerDataFromJson("dummy-file.json");

        // Assert
        verify(mapEntityRepo, times(1)).save(any(MapEntity.class));
    }

    @Test
    @DisplayName("Should create new marker type if Bunker type does not exist")
    void testCreatesNewMarkerTypeIfNotExists() throws Exception {
        JsonNode rootNode = mock(JsonNode.class);
        when(rootNode.get("features")).thenReturn(mock(JsonNode.class));
        when(rootNode.get("features").iterator()).thenReturn(List.<JsonNode>of().iterator());

        when(mapMarkerTypeRepo.findByName("Bunker")).thenReturn(Optional.empty());
        when(mapMarkerTypeRepo.save(any(MapMarkerType.class))).thenReturn(new MapMarkerType("Bunker"));

        BunkerImportService spyService = Mockito.spy(bunkerImportService);
        doReturn(rootNode).when(spyService).readJsonFromFile(anyString());

        spyService.importBunkerDataFromJson("anyfile.json");

        verify(mapMarkerTypeRepo, times(1)).save(any(MapMarkerType.class));
    }

    @Test
    @DisplayName("Should throw IOException when file not found")
    void testThrowsIOException() throws Exception {
        BunkerImportService spyService = Mockito.spy(bunkerImportService);

        doThrow(new IOException("File not found")).when(spyService).readJsonFromFile(anyString());

        assertThrows(IOException.class, () -> {
            spyService.importBunkerDataFromJson("invalid.json");
        });
    }

    @Test
    @DisplayName("Should read JSON file successfully with readJsonFromFile")
    void testReadJsonFromFileSuccess() throws Exception {
        // Use a real ObjectMapper
        ObjectMapper realObjectMapper = new ObjectMapper();

        // Create a new BunkerImportService with the real ObjectMapper
        BunkerImportService service = new BunkerImportService(mapEntityRepo,
            mapMarkerTypeRepo, realObjectMapper, mapEntityTypeRepo);

        String fileName = "testfile.json";

        JsonNode jsonNode = service.readJsonFromFile(fileName);

        assertNotNull(jsonNode);
        assertTrue(jsonNode.has("features"), "JSON root should have 'features' node");
    }
}