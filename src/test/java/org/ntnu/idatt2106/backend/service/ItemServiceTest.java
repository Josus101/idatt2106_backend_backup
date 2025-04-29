package org.ntnu.idatt2106.backend.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ntnu.idatt2106.backend.dto.item.ItemCreateRequest;
import org.ntnu.idatt2106.backend.dto.item.ItemGenericDTO;
import org.ntnu.idatt2106.backend.model.Category;
import org.ntnu.idatt2106.backend.model.Item;
import org.ntnu.idatt2106.backend.model.Unit;
import org.ntnu.idatt2106.backend.repo.CategoryRepo;
import org.ntnu.idatt2106.backend.repo.ItemRepo;
import org.ntnu.idatt2106.backend.repo.UnitRepo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the ItemService class.
 * This class contains tests for all methods in the ItemService class.
 *
 * @version 1.0
 * @since 1.0
 * @author Jonas Reiher
 */
public class ItemServiceTest {

  @InjectMocks
  private ItemService itemService;

  @Mock
  private ItemRepo itemRepo;

  @Mock
  private UnitRepo unitRepo;

  @Mock
  private CategoryRepo categoryRepo;

  private Item testItem;

  private ItemGenericDTO testItemDTO;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    testItem = new Item("Water", 1.0, new Unit("Liter"), new Date());

    testItem.setId(1);

    Unit testItemUnit = new Unit("Liters");
    testItemUnit.setId(111);
    testItem.setUnit(testItemUnit);



    Category testItemCategory = new Category(1, "Liquid", 0, false);
    testItemCategory.setId(222);
    testItem.setCategory(testItemCategory);

    Date testItemExpirationDate = new Date();
    testItem.setExpirationDate(testItemExpirationDate);

    this.testItemDTO = new ItemGenericDTO(
            testItem.getId(),
            testItem.getName(),
            testItem.getAmount(),
            testItem.getUnit().getId(),
            testItem.getCategory().getId(),
            testItem.getExpirationDate()
    );
  }

  @Test
  @DisplayName("getItemById should return item with requested id")
  void testGetItemByIdSuccess() {
    when(itemRepo.findById(1)).thenReturn(Optional.of(testItem));

    ItemGenericDTO itemDTO = itemService.getItemById(1);

    assertNotNull(itemDTO);
    assertEquals(itemDTO.getId(), testItemDTO.getId());
    assertEquals(itemDTO.getName(), testItemDTO.getName());
    assertEquals(itemDTO.getAmount(), testItemDTO.getAmount());
    assertEquals(itemDTO.getUnitId(), testItemDTO.getUnitId());
    assertEquals(itemDTO.getCategoryId(), testItemDTO.getCategoryId());
    assertEquals(itemDTO.getExpirationDate(), testItemDTO.getExpirationDate());
  }

  @Test
  @DisplayName("getItemById should throw EntityNotFoundException if user does not exist")
  void testGetItemByIdNotFound() {
    when(itemRepo.findById(1)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> itemService.getItemById(1));
  }

  @Test
  @DisplayName("getItemByHouseholdId should return list of items with requested household id")
  void testGetItemByHouseholdIdSuccess() {
    List<ItemGenericDTO> itemDTOList = new ArrayList<>();
    itemDTOList.add(testItemDTO);

    List<Item> itemList = new ArrayList<>();
    itemList.add(testItem);

    when(itemRepo.findByHousehold_Id(1)).thenReturn(Optional.of(itemList));

    assertEquals(1, itemService.getItemsByHouseholdId(1).size());
    assertEquals(itemDTOList.get(0).getId(), itemService.getItemsByHouseholdId(1).get(0).getId());
  }

  @Test
  @DisplayName("getItemByHouseholdId should throw EntityNotFoundException if no items for given household exists")
  void getItemByHouseholdIdNotFound() {
    when(itemRepo.findByHousehold_Id(1)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> itemService.getItemsByHouseholdId(1));
  }

  @Test
  @DisplayName("getItemByCategoryId should return list of items with requested category id")
  void getItemByCategoryIdSuccess() {
    List<ItemGenericDTO> itemDTOList = new ArrayList<>();
    itemDTOList.add(testItemDTO);

    List<Item> itemList = new ArrayList<>();
    itemList.add(testItem);

    when(itemRepo.findByCategory_Id(1)).thenReturn(Optional.of(itemList));

    assertEquals(1, itemService.getItemsByCategoryId(1).size());
    assertEquals(itemDTOList.get(0).getId(), itemService.getItemsByCategoryId(1).get(0).getId());
  }

  @Test
  @DisplayName("getItemByCategoryId should throw EntityNotFoundException if no items for given category exists")
  void getItemByCategoryIdNotFound() {
    when(itemRepo.findByCategory_Id(1)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> itemService.getItemsByCategoryId(1));
  }

  @Test
  @DisplayName("getAllItems should return list of all items")
  void getAllItemsSuccess() {
    List<ItemGenericDTO> itemDTOList = new ArrayList<>();
    itemDTOList.add(testItemDTO);

    List<Item> itemList = new ArrayList<>();
    itemList.add(testItem);

    when(itemRepo.findAll()).thenReturn(itemList);

    assertEquals(1, itemService.getAllItems().size());
    assertEquals(itemDTOList.get(0).getId(), itemService.getAllItems().get(0).getId());
  }

  @Test
  @DisplayName("getAllItems should return empty list on no items found")
  void getAllItemsNotFound() {
   List<Item> emptyItemList = new ArrayList<>();

   when(itemRepo.findAll()).thenReturn(emptyItemList);

   assertTrue(itemService.getAllItems().isEmpty());
  }

  @Test
  @DisplayName("AddItem should add a new item successfully")
  void addItemSuccess() {
    Date expirationDate = new Date();
    ItemCreateRequest itemCreateRequest = new ItemCreateRequest("Melk", 1.75, 111, 222, expirationDate);

    Unit mockUnit = new Unit();
    mockUnit.setId(111);
    mockUnit.setName("Liter");

    Category mockCategory = new Category();
    mockCategory.setId(222);
    mockCategory.setName("Dairy");
    mockCategory.setKcalPerUnit(0);
    mockCategory.setIsEssential(false);

    Item savedItem = new Item();
    savedItem.setId(1);
    savedItem.setName("Melk");
    savedItem.setAmount(1.75);
    savedItem.setUnit(mockUnit);
    savedItem.setCategory(mockCategory);
    savedItem.setExpirationDate(expirationDate);

    when(unitRepo.findById(111)).thenReturn(Optional.of(mockUnit));
    when(categoryRepo.findById(222)).thenReturn(Optional.of(mockCategory));
    when(itemRepo.save(any(Item.class))).thenReturn(savedItem);

    ItemGenericDTO result = itemService.addItem(itemCreateRequest);

    assertNotNull(result);
    assertEquals("Melk", result.getName());
    assertEquals(1.75, result.getAmount());
    assertEquals(111, result.getUnitId());
    assertEquals(222, result.getCategoryId());
    assertEquals(expirationDate, result.getExpirationDate());
  }

  @Test
  @DisplayName("AddItem should throw EntityNotFoundException if unit does not exist")
  void addItemUnitNotFound() {
    ItemCreateRequest itemCreateRequest = new ItemCreateRequest("Melk", 1.75, 111, 222, new Date());

    when(unitRepo.findById(111)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> itemService.addItem(itemCreateRequest));
  }

  @Test
  @DisplayName("AddItem should throw EntityNotFoundException if category does not exist")
  void addItemCategoryNotFound() {
    ItemCreateRequest itemCreateRequest = new ItemCreateRequest("Melk", 1.75, 111, 222, new Date());

    Unit mockUnit = new Unit();
    mockUnit.setId(111);
    mockUnit.setName("Liter");

    when(unitRepo.findById(111)).thenReturn(Optional.of(mockUnit));
    when(categoryRepo.findById(222)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> itemService.addItem(itemCreateRequest));
  }

  @Test
  @DisplayName("UpdateItem should update an existing item successfully")
  void updateItemSuccess() {
    // Arrange
    ItemGenericDTO newItemData = new ItemGenericDTO(1, "Hel Melk", 1.75, 111, 222, new Date());

    Unit mockUnit = new Unit();
    mockUnit.setId(111);
    mockUnit.setName("Liter");

    Category mockCategory = new Category();
    mockCategory.setId(222);
    mockCategory.setName("Dairy");
    mockCategory.setKcalPerUnit(0);
    mockCategory.setIsEssential(false);

    Item updatedItem = new Item();
    updatedItem.setId(1);
    updatedItem.setName("Hel Melk");
    updatedItem.setAmount(1.75);
    updatedItem.setUnit(mockUnit);
    updatedItem.setCategory(mockCategory);
    updatedItem.setExpirationDate(new Date());

    when(itemRepo.findById(1)).thenReturn(Optional.of(testItem));
    when(unitRepo.findById(111)).thenReturn(Optional.of(mockUnit));
    when(categoryRepo.findById(222)).thenReturn(Optional.of(mockCategory));
    when(itemRepo.save(any(Item.class))).thenReturn(updatedItem);

    itemService.updateItem(newItemData);

    assertEquals("Hel Melk", updatedItem.getName());
  }

  @Test
  @DisplayName("UpdateItem should throw EntityNotFoundException if item does not exist")
  void updateItemNotFound() {
    ItemGenericDTO newItemData = new ItemGenericDTO(1, "Hel Melk", 1.75, 111, 222, new Date());

    when(itemRepo.findById(1)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(newItemData));
  }

  @Test
  @DisplayName("UpdateItem should throw EntityNotFoundException if unit does not exist")
  void updateItemUnitNotFound() {
    ItemGenericDTO newItemData = new ItemGenericDTO(1, "Hel Melk", 1.75, 111, 222, new Date());

    when(itemRepo.findById(1)).thenReturn(Optional.of(testItem));
    when(unitRepo.findById(111)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(newItemData));
  }

  @Test
  @DisplayName("UpdateItem should throw EntityNotFoundException if category does not exist")
  void updateItemCategoryNotFound() {
    ItemGenericDTO newItemData = new ItemGenericDTO(1, "Hel Melk", 1.75, 111, 222, new Date());

    when(itemRepo.findById(1)).thenReturn(Optional.of(testItem));
    when(unitRepo.findById(111)).thenReturn(Optional.of(new Unit()));
    when(categoryRepo.findById(222)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(newItemData));
  }

  @Test
  @DisplayName("deleteItem should successfully remove an item")
  void deleteItemSuccess() {
    int itemId = 1;

    Item mockItem = new Item();
    mockItem.setId(itemId);
    mockItem.setHousehold(new ArrayList<>()); // Set an empty household list

    when(itemRepo.findById(itemId)).thenReturn(Optional.of(mockItem));

    itemService.deleteItem(itemId);

    verify(itemRepo).findById(itemId);     // Verify that findById was called
    verify(itemRepo).save(mockItem);        // Verify that save was called after clearing households
    verify(itemRepo).delete(mockItem);      // Verify that delete was called
  }

  @Test
  @DisplayName("DeleteItem should throw EntityNotFoundException if item does not exist")
  void deleteItemNotFound() {
    when(itemRepo.findById(1)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> itemService.deleteItem(1));
  }
}