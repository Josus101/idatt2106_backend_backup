package org.ntnu.idatt2106.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.ntnu.idatt2106.backend.dto.household.EssentialItemStatusDTO;
import org.ntnu.idatt2106.backend.dto.household.PreparednessStatus;
import org.ntnu.idatt2106.backend.service.EssentialItemService;
import org.ntnu.idatt2106.backend.service.PreparednessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HouseholdControllerTest {

    @InjectMocks
    private HouseholdController householdController;

    @Mock
    private PreparednessService preparednessService;

    @Mock
    private EssentialItemService essentialItemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test getPreparednessStatus returns status for valid household ID")
    void testGetPreparednessStatusSuccess() {
        int householdId = 1;
        PreparednessStatus status = new PreparednessStatus(8, 3);

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

    @Test
    @DisplayName("Test getEssentialItemsStatus returns list on success")
    void testGetEssentialItemsStatusSuccess() {
        int householdId = 1;
        List<EssentialItemStatusDTO> mockList = List.of(
                new EssentialItemStatusDTO("grill", true),
                new EssentialItemStatusDTO("jodtabletter", false)
        );

        when(essentialItemService.getEssentialItemStatus(householdId)).thenReturn(mockList);

        ResponseEntity<?> response = householdController.getEssentialItemsStatus(householdId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockList, response.getBody());
    }

    @Test
    @DisplayName("Test getEssentialItemsStatus returns 404 if household not found")
    void testGetEssentialItemsStatusNotFound() {
        int householdId = 404;

        when(essentialItemService.getEssentialItemStatus(householdId))
                .thenThrow(new NoSuchElementException("Household not found"));

        ResponseEntity<?> response = householdController.getEssentialItemsStatus(householdId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Household not found: Household not found", response.getBody());
    }

    @Test
    @DisplayName("Test getEssentialItemsStatus returns 500 on error")
    void testGetEssentialItemsStatusInternalError() {
        int householdId = 500;

        when(essentialItemService.getEssentialItemStatus(householdId))
                .thenThrow(new RuntimeException("DB failure"));

        ResponseEntity<?> response = householdController.getEssentialItemsStatus(householdId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to fetch essential items: DB failure", response.getBody());
    }
}
