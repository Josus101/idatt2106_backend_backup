package org.ntnu.idatt2106.backend.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ntnu.idatt2106.backend.dto.unit.UnitGetResponse;
import org.ntnu.idatt2106.backend.model.Unit;
import org.ntnu.idatt2106.backend.repo.UnitRepo;
import org.springframework.test.context.bean.override.BeanOverride;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * UnitServiceTest is a test class for the UnitService class.
 * It contains test methods to verify the functionality of the UnitService class.
 * The tests are currently not implemented.
 * @author Jonas Reiher
 * @version 0.1
 */
public class UnitServiceTest {

  @InjectMocks
  private UnitService unitService;

  @Mock
  private UnitRepo unitRepo;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }
/*
  @Test
  @DisplayName("getUnitById should return a UnitGetResponse when unit exists")
  void getUnitByIdSuccess() {
    int unitId = 1;
    Unit mockUnit = new Unit();
    mockUnit.setId(unitId);
    mockUnit.setName("Liter");

    when(unitRepo.findById(unitId)).thenReturn(Optional.of(mockUnit));

    UnitGetResponse result = unitService.getUnitById(unitId);

    assertNotNull(result);
    assertEquals(unitId, result.getId());
    assertEquals("Liter", result.getName());
  }*/

  @Test
  @DisplayName("getUnitById should throw an exception if the unit is not found")
  void getUnitByIdNotFound() {
    when(unitRepo.findById(1)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> unitService.getUnitById(1));
  }
/*
  @Test
  @DisplayName("getAllUnits should return all units")
  void getAllUnitsSuccess() {
    Unit unit1 = new Unit();
    unit1.setId(1);
    unit1.setName("Liter");

    Unit unit2 = new Unit();
    unit2.setId(2);
    unit2.setName("Kilogram");

    when(unitRepo.findAll()).thenReturn(List.of(unit1, unit2));

    List<UnitGetResponse> result = unitService.getAllUnits();

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("Liter", result.get(0).getName());
    assertEquals("Kilogram", result.get(1).getName());
  }*/

  @Test
  @DisplayName("getAllUnits should return an empty list if no units are found")
  void getAllUnitsEmpty() {
    when(unitRepo.findAll()).thenReturn(List.of());

    List<UnitGetResponse> result = unitService.getAllUnits();

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
}
