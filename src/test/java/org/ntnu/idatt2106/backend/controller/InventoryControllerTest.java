package org.ntnu.idatt2106.backend.controller;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ntnu.idatt2106.backend.dto.item.ItemCreateRequest;
import org.ntnu.idatt2106.backend.dto.item.ItemGenericDTO;
import org.ntnu.idatt2106.backend.exceptions.UserNotFoundException;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.repo.ItemRepo;
import org.ntnu.idatt2106.backend.security.JWT_token;
import org.ntnu.idatt2106.backend.service.CategoryService;
import org.ntnu.idatt2106.backend.service.ItemService;
import org.ntnu.idatt2106.backend.service.UnitService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the InventoryController class.
 */
public class InventoryControllerTest {

  @InjectMocks
  private InventoryController inventoryController;

  @Mock
  private ItemService itemService;

  @Mock
  private ItemRepo itemRepo;

  @Mock
  private UnitService unitService;

  @Mock
  private CategoryService categoryService;

  @Mock
  private JWT_token jwtToken;

  private ItemGenericDTO itemGenericDTO;
  private String validAuthHeader;
  private User testUser;
  private static final int TEST_USER_ID = 1;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    MockMvcBuilders.standaloneSetup(inventoryController).build();

    // Setup test user
    testUser = new User();
    testUser.setId(TEST_USER_ID);

    // Setup auth header
    validAuthHeader = "Bearer valid-token";

    // Mock JWT token validation
    try {
      when(jwtToken.getUserByToken("valid-token")).thenReturn(testUser);
    } catch (UserNotFoundException e) {
      fail("User should be found in setup");
    }

    // Setup household IDs
    List<Integer> householdIds = new ArrayList<>();
    householdIds.add(1);

    itemGenericDTO = new ItemGenericDTO(
        1,
        "Test Item",
        10.0,
        1,
        1,
        new Date(),
        householdIds
    );
  }

  @Test
  @DisplayName("getItem method returns success on valid item ID")
  void getItemByIdSuccess() {
    when(itemService.getItemById(anyInt(), anyInt())).thenReturn(itemGenericDTO);

    ResponseEntity<?> response = inventoryController.getItem(1, validAuthHeader);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(itemGenericDTO, response.getBody());
  }

  @Test
  @DisplayName("getItem method returns not found on invalid item ID")
  void getItemByIdNotFound() {
    when(itemService.getItemById(anyInt(), anyInt())).thenThrow(new EntityNotFoundException("Item not found"));

    ResponseEntity<?> response = inventoryController.getItem(1, validAuthHeader);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Error: Unauthorized", response.getBody());
  }

  @Test
  @DisplayName("addItem method returns success on valid item creation")
  void addItemSuccess() {
    List<Integer> householdIds = new ArrayList<>();
    householdIds.add(1);

    ItemCreateRequest itemCreateRequest = new ItemCreateRequest(
        "Test Item",
        10.0,
        1,
        1,
        new Date(),
        householdIds
    );

    when(itemService.addItem(any(ItemCreateRequest.class), anyInt())).thenReturn(itemGenericDTO);

    ResponseEntity<?> response = inventoryController.addItem(itemCreateRequest, validAuthHeader);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(itemGenericDTO, response.getBody());
  }

  @Test
  @DisplayName("addItem method returns bad request on invalid unit")
  void addItemBadRequestUnit() {
    List<Integer> householdIds = new ArrayList<>();
    householdIds.add(1);

    ItemCreateRequest itemCreateRequest = new ItemCreateRequest(
        "Test Item",
        10.0,
        999,
        1,
        new Date(),
        householdIds
    );

    when(itemService.addItem(any(ItemCreateRequest.class), anyInt())).thenThrow(new IllegalArgumentException("Unit not found"));

    ResponseEntity<?> response = inventoryController.addItem(itemCreateRequest, validAuthHeader);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Unit not found", response.getBody());
  }

  @Test
  @DisplayName("addItem method returns bad request on invalid category")
  void addItemBadRequestCategory() {
    List<Integer> householdIds = new ArrayList<>();
    householdIds.add(1);

    ItemCreateRequest itemCreateRequest = new ItemCreateRequest(
        "Test Item",
        10.0,
        1,
        999,
        new Date(),
        householdIds
    );

    when(itemService.addItem(any(ItemCreateRequest.class), anyInt())).thenThrow(new EntityNotFoundException("Category not found"));

    ResponseEntity<?> response = inventoryController.addItem(itemCreateRequest, validAuthHeader);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Error: Unauthorized", response.getBody());
  }

  @Test
  @DisplayName("updateItem method returns success on valid item update")
  void updateItemSuccess() {
    when(itemService.updateItem(any(ItemGenericDTO.class), anyInt())).thenReturn(itemGenericDTO);

    ResponseEntity<?> response = inventoryController.updateItem(itemGenericDTO, validAuthHeader);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(itemGenericDTO, response.getBody());
  }

  @Test
  @DisplayName("updateItem method returns bad request on invalid item update")
  void updateItemBadRequest() {
    when(itemService.updateItem(any(ItemGenericDTO.class), anyInt())).thenThrow(new IllegalArgumentException("Invalid item data"));

    ResponseEntity<?> response = inventoryController.updateItem(itemGenericDTO, validAuthHeader);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Error: Invalid item data", response.getBody());
  }

  @Test
  @DisplayName("deleteItem method returns success on successful deletion")
  void deleteItemSuccess() {
    doNothing().when(itemService).deleteItem(anyInt(), anyInt());

    ResponseEntity<String> response = inventoryController.deleteItem(1, validAuthHeader);

    verify(itemService).deleteItem(eq(1), eq(TEST_USER_ID));
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Item deleted successfully", response.getBody());
  }

  @Test
  @DisplayName("deleteItem method returns not found on invalid item ID")
  void deleteItemNotFound() {
    doThrow(new UserNotFoundException("Item not found")).when(itemService).deleteItem(anyInt(), anyInt());

    ResponseEntity<String> response = inventoryController.deleteItem(1, validAuthHeader);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error: Item not found", response.getBody());
  }

  @Test
  @DisplayName("getItemsByCategory method returns success on valid request")
  void getItemsByCategorySuccess() {
    List<ItemGenericDTO> items = List.of(itemGenericDTO);
    when(itemService.getItemsByCategoryId(anyInt(), anyInt())).thenReturn(items);

    ResponseEntity<?> response = inventoryController.getItemsByCategory(1, validAuthHeader);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(items, response.getBody());
  }

  @Test
  @DisplayName("getItemsByCategory method returns UNAUTHORIZED on unauthorized category ID")
  void getItemsByCategoryNotFound() {
    when(itemService.getItemsByCategoryId(anyInt(), anyInt())).thenThrow(new EntityNotFoundException("Category not found"));

    ResponseEntity<?> response = inventoryController.getItemsByCategory(1, validAuthHeader);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Error: Unauthorized", response.getBody());
  }

  @Test
  @DisplayName("getItemsByHousehold method returns items on success")
  void getItemsByHouseholdSuccess() {
    List<ItemGenericDTO> items = List.of(itemGenericDTO);
    when(itemService.getItemsByHouseholdId(anyInt(), anyInt())).thenReturn(items);

    ResponseEntity<?> response = inventoryController.getInventoryForHousehold(1, validAuthHeader);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(items, response.getBody());
  }

  @Test
  @DisplayName("getItemsByHousehold method returns not found on no existing items")
  void getItemsByHouseholdNotFound() {
    when(itemService.getItemsByHouseholdId(anyInt(), anyInt())).thenThrow(new UserNotFoundException("No items found for this household"));

    ResponseEntity<?> response = inventoryController.getInventoryForHousehold(1, validAuthHeader);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error: No items found for this household", response.getBody());
  }

  @Test
  @DisplayName("getItemsByHousehold method returns unauthorized on invalid token")
  void getItemsByHouseholdUnauthorized() {
    when(jwtToken.getUserByToken("invalid-token")).thenThrow(new IllegalArgumentException("Invalid token"));

    ResponseEntity<?> response = inventoryController.getInventoryForHousehold(1, "Bearer invalid-token");

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Error: Unauthorized", response.getBody());
  }

  @Test
  @DisplayName("getInventory method returns success on valid request")
  void getInventorySuccess() {
    List<ItemGenericDTO> items = List.of(itemGenericDTO);
    when(itemService.getAllItems()).thenReturn(items);

    ResponseEntity<?> response = inventoryController.getInventory();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(items, response.getBody());
  }

  @Test
  @DisplayName("getInventory method returns not found when no items exist")
  void getInventoryNotFound() {
    when(itemService.getAllItems()).thenThrow(new IllegalArgumentException("No items found"));

    ResponseEntity<?> response = inventoryController.getInventory();

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error: No items found", response.getBody());
  }
}