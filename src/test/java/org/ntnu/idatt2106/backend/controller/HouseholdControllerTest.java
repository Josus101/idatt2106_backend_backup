package org.ntnu.idatt2106.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.ntnu.idatt2106.backend.dto.household.PreparednessStatus;
import org.ntnu.idatt2106.backend.model.Household;
import org.ntnu.idatt2106.backend.repo.HouseholdRepo;
import org.ntnu.idatt2106.backend.service.PreparednessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HouseholdControllerTest {

    @InjectMocks
    private HouseholdController householdController;

    @Mock
    private HouseholdRepo householdRepo;

    @Mock
    private PreparednessService preparednessService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test getPreparednessStatus returns status for valid household ID")
    void testGetPreparednessStatusSuccess() {
        int householdId = 1;
        Household household = new Household();
        PreparednessStatus status = new PreparednessStatus(80, false, "Good");

        when(householdRepo.findById(householdId)).thenReturn(java.util.Optional.of(household));
        when(preparednessService.calculatePreparednessStatus(household)).thenReturn(status);

        ResponseEntity<PreparednessStatus> response = householdController.getPreparednessStatus(householdId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(status, response.getBody());
    }

    @Test
    @DisplayName("Test getPreparednessStatus throws 404 for invalid household ID")
    void testGetPreparednessStatusNotFound() {
        int householdId = 999;

        when(householdRepo.findById(householdId)).thenReturn(java.util.Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            householdController.getPreparednessStatus(householdId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Household not found", exception.getReason());
    }
}
