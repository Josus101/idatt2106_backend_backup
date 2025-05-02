package org.ntnu.idatt2106.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.ntnu.idatt2106.backend.dto.household.EssentialItemStatusDTO;
import org.ntnu.idatt2106.backend.dto.household.PreparednessStatus;
import org.ntnu.idatt2106.backend.security.JWT_token;
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

    @Mock
    private JWT_token jwtToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return preparedness status from token")
    void testGetPreparednessStatusSuccess() {
        String token = "Bearer dummy.jwt.token";
        int userId = 42;
        PreparednessStatus mockStatus = new PreparednessStatus(6.0, 4.0);

        when(jwtToken.extractIdFromJwt("dummy.jwt.token")).thenReturn(String.valueOf(userId));
        when(preparednessService.getPreparednessStatusByUserId(userId)).thenReturn(List.of(mockStatus));

        ResponseEntity<?> response = householdController.getPreparednessStatus(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(mockStatus), response.getBody());
    }

    @Test
    @DisplayName("Should return 404 if user has no households")
    void testGetPreparednessStatusUserNotFound() {
        String token = "Bearer abc.def.ghi";

        when(jwtToken.extractIdFromJwt("abc.def.ghi")).thenReturn("99");
        when(preparednessService.getPreparednessStatusByUserId(99)).thenThrow(new NoSuchElementException("User not found"));

        ResponseEntity<?> response = householdController.getPreparednessStatus(token);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Error: User not found", response.getBody());
    }

    @Test
    @DisplayName("Should return 500 on unknown error in preparedness")
    void testGetPreparednessStatusInternalError() {
        String token = "Bearer xyz.token";
        when(jwtToken.extractIdFromJwt("xyz.token")).thenReturn("123");
        when(preparednessService.getPreparednessStatusByUserId(123)).thenThrow(new RuntimeException("Boom"));

        ResponseEntity<?> response = householdController.getPreparednessStatus(token);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error: Boom", response.getBody());
    }

    @Test
    @DisplayName("Should return essential items status from token")
    void testGetEssentialItemsStatusSuccess() {
        String token = "Bearer real.jwt.token";
        int userId = 24;

        List<List<EssentialItemStatusDTO>> mockItems = List.of(
                List.of(
                        new EssentialItemStatusDTO("grill", true),
                        new EssentialItemStatusDTO("jodtabletter", false)
                )
        );

        when(jwtToken.extractIdFromJwt("real.jwt.token")).thenReturn(String.valueOf(userId));
        when(essentialItemService.getEssentialItemStatusByUserId(userId)).thenReturn(mockItems);

        ResponseEntity<?> response = householdController.getEssentialItemsStatus(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockItems, response.getBody());
    }

    @Test
    @DisplayName("Should return 404 if user has no essential items")
    void testGetEssentialItemsStatusNotFound() {
        String token = "Bearer token.test";
        when(jwtToken.extractIdFromJwt("token.test")).thenReturn("88");
        when(essentialItemService.getEssentialItemStatusByUserId(88)).thenThrow(new NoSuchElementException("Household not found"));

        ResponseEntity<?> response = householdController.getEssentialItemsStatus(token);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Error: Household not found", response.getBody());
    }

    @Test
    @DisplayName("Should return 500 on essential item service failure")
    void testGetEssentialItemsStatusInternalError() {
        String token = "Bearer abc.123";
        when(jwtToken.extractIdFromJwt("abc.123")).thenReturn("55");
        when(essentialItemService.getEssentialItemStatusByUserId(55)).thenThrow(new RuntimeException("DB failure"));

        ResponseEntity<?> response = householdController.getEssentialItemsStatus(token);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected error: DB failure", response.getBody());
    }
}
