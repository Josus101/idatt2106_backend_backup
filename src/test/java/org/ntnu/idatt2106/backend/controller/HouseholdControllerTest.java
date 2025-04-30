package org.ntnu.idatt2106.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.ntnu.idatt2106.backend.dto.household.PreparednessStatus;
import org.ntnu.idatt2106.backend.service.PreparednessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HouseholdControllerTest {

    @InjectMocks
    private HouseholdController householdController;

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
        PreparednessStatus status = new PreparednessStatus(80, false, "Good");

        when(preparednessService.getPreparednessStatusByHouseholdId(householdId)).thenReturn(status);

        ResponseEntity<?> response = householdController.getPreparednessStatus(householdId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(status, response.getBody());
    }

    @Test
    @DisplayName("Test getPreparednessStatus returns 404 if household not found")
    void testGetPreparednessStatusNotFound() {
        int householdId = 999;

        when(preparednessService.getPreparednessStatusByHouseholdId(householdId))
                .thenThrow(new NoSuchElementException("Household not found"));

        ResponseEntity<?> response = householdController.getPreparednessStatus(householdId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Error: Household not found", response.getBody());
    }

    @Test
    @DisplayName("Test getPreparednessStatus returns 500 on general exception")
    void testGetPreparednessStatusInternalError() {
        int householdId = 123;

        when(preparednessService.getPreparednessStatusByHouseholdId(householdId))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<?> response = householdController.getPreparednessStatus(householdId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error: Could not fetch preparedness status", response.getBody());
    }
}
