package org.ntnu.idatt2106.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.ntnu.idatt2106.backend.service.BunkerImportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BunkerImportControllerTest {
    @InjectMocks
    private BunkerImportController bunkerImportController;

    @Mock
    private BunkerImportService bunkerImportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return 200 when bunker data is imported successfully")
    void testImportBunkerDataSuccess() throws Exception {
        ResponseEntity<String> response = bunkerImportController.importBunkerData();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Bunker data imported successfully", response.getBody());
        verify(bunkerImportService, times(1))
                .importBunkerDataFromJson("Samfunnssikkerhet_0000_Norge_25833_TilfluktsromOffentlige_GeoJSON.json");
    }

    @Test
    @DisplayName("Should return 500 when exception occurs during bunker data import")
    void testImportBunkerDataFailure() throws Exception {
        doThrow(new RuntimeException("Import error")).when(bunkerImportService)
                .importBunkerDataFromJson(anyString());

        ResponseEntity<String> response = bunkerImportController.importBunkerData();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Import failed:"));
    }

    @Test
    @DisplayName("Should return 500 and IO error message when IOException occurs during bunker data import")
    void testImportBunkerDataIOException() throws Exception {
        // Arrange: Mock service to throw IOException
        doThrow(new IOException("Simulated IO failure"))
                .when(bunkerImportService)
                .importBunkerDataFromJson(anyString());

        // Act
        ResponseEntity<String> response = bunkerImportController.importBunkerData();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Import failed due to IO error:"));
        assertTrue(response.getBody().contains("Simulated IO failure"));
    }


}