package org.ntnu.idatt2106.backend.controller;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ntnu.idatt2106.backend.dto.unit.UnitGetResponse;
import org.ntnu.idatt2106.backend.service.UnitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the UnitController class.
 */
class UnitControllerTest {
  @InjectMocks
  private UnitController unitController;
  @Mock
  UnitService unitService;
  private UnitGetResponse unitGetResponse;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    MockMvcBuilders.standaloneSetup(unitController).build();
    unitGetResponse = new UnitGetResponse(
            1,
            "Test Unit"
    );
  }

  @Test
  @DisplayName("getUnits method returns success on existing units")
  void getUnitsSuccess() {
    List<UnitGetResponse> units = List.of(unitGetResponse);
    when(unitService.getAllUnits()).thenReturn(units);

    ResponseEntity<?> response = unitController.getAllUnits();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(units, response.getBody());
  }

  @Test
  @DisplayName("getUnits method returns not found on no existing units")
  void getUnitsNotFound() {
    when(unitService.getAllUnits()).thenReturn(List.of());

    ResponseEntity<?> response = unitController.getAllUnits();

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error: No units found", response.getBody());
  }

  @Test
  @DisplayName("getUnitById method returns success on existing Unit")
  void getUnitByIdSuccess() {
    when(unitService.getUnitById(1)).thenReturn(unitGetResponse);

    ResponseEntity<?> response = unitController.getUnitById(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(unitGetResponse, response.getBody());
  }

  @Test
  @DisplayName("getUnitById method returns not found on non-existing Unit")
  void getUnitByIdNotFound() {
    when(unitService.getUnitById(1)).thenThrow(new EntityNotFoundException("Unit not found"));

    ResponseEntity<?> response = unitController.getUnitById(1);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error: Unit not found", response.getBody());
  }

}