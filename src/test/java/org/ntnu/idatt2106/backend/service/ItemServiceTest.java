package org.ntnu.idatt2106.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ntnu.idatt2106.backend.dto.item.ItemCreateRequest;
import org.ntnu.idatt2106.backend.dto.item.ItemGenericDTO;
import org.ntnu.idatt2106.backend.model.Category;
import org.ntnu.idatt2106.backend.model.Household;
import org.ntnu.idatt2106.backend.model.HouseholdMembers;
import org.ntnu.idatt2106.backend.model.HouseholdMembersId;
import org.ntnu.idatt2106.backend.model.Item;
import org.ntnu.idatt2106.backend.model.Unit;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.repo.CategoryRepo;
import org.ntnu.idatt2106.backend.repo.HouseholdRepo;
import org.ntnu.idatt2106.backend.repo.ItemRepo;
import org.ntnu.idatt2106.backend.repo.UnitRepo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

  @Mock
  private HouseholdRepo householdRepo;

  private Item testItem;
  private ItemGenericDTO testItemDTO;
  private List<Household> households;
  private User testUser;
  private final int TEST_USER_ID = 1;
  private final int TEST_HOUSEHOLD_ID = 1;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    testUser = new User();
    testUser.setId(TEST_USER_ID);

    Household testHousehold = new Household();
    testHousehold.setId(TEST_HOUSEHOLD_ID);
    testHousehold.setName("Test Household");

    HouseholdMembers testMember = new HouseholdMembers();
    testMember.setUser(testUser);
    testMember.setHousehold(testHousehold);
    testMember.setAdmin(true);
    testMember.setId(new HouseholdMembersId(TEST_USER_ID, TEST_HOUSEHOLD_ID));

    List<HouseholdMembers> members = new ArrayList<>();
    members.add(testMember);
    testHousehold.setMembers(members);

    households = new ArrayList<>();
    households.add(testHousehold);

    testItem = new Item("Water", 1.0, new Unit("Liter", "Liter"), new Date());
    testItem.setId(1);
    testItem.setHousehold(households);

    Unit testItemUnit = new Unit("Liters", "Liter");
    testItemUnit.setId(111);
    testItem.setUnit(testItemUnit);

    Category testItemCategory = new Category(1, "Liquid", "Væske", 0, false);
    testItemCategory.setId(222);
    testItem.setCategory(testItemCategory);

    Date testItemExpirationDate = new Date();
    testItem.setExpirationDate(testItemExpirationDate);

    List<Integer> householdIds = new ArrayList<>();
    householdIds.add(TEST_HOUSEHOLD_ID);

    this.testItemDTO = new ItemGenericDTO(
        testItem.getId(),
        testItem.getName(),
        testItem.getAmount(),
        testItem.getUnit().getId(),
        testItem.getCategory().getId(),
        testItem.getExpirationDate(),
        householdIds
    );

    when(householdRepo.findById(TEST_HOUSEHOLD_ID)).thenReturn(Optional.of(testHousehold));
  }

  @Test
  @DisplayName("getItemById should return item with requested id")
  void testGetItemByIdSuccess() {
    when(itemRepo.findById(1)).thenReturn(Optional.of(testItem));

    ItemGenericDTO itemDTO = itemService.getItemById(1, TEST_USER_ID);

    assertNotNull(itemDTO);
    assertEquals(itemDTO.getId(), testItemDTO.getId());
    assertEquals(itemDTO.getName(), testItemDTO.getName());
    assertEquals(itemDTO.getAmount(), testItemDTO.getAmount());
    assertEquals(itemDTO.getUnitId(), testItemDTO.getUnitId());
    assertEquals(itemDTO.getCategoryId(), testItemDTO.getCategoryId());
    assertEquals(itemDTO.getExpirationDate(), testItemDTO.getExpirationDate());
  }

  @Test
  @DisplayName("getItemById should throw IllegalArgumentException if item does not exist")
  void testGetItemByIdNotFound() {
    when(itemRepo.findById(1)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> itemService.getItemById(1, TEST_USER_ID));
  }

  @Test
  @DisplayName("getItemById should throw IllegalArgumentException if user is not authorized")
  void testGetItemByIdUnauthorized() {
    when(itemRepo.findById(1)).thenReturn(Optional.of(testItem));

    assertThrows(IllegalArgumentException.class, () -> itemService.getItemById(1, 999));
  }

  @Test
  @DisplayName("getItemByHouseholdId should return list of items with requested household id")
  void testGetItemsByHouseholdIdSuccess() {
    List<Item> itemList = new ArrayList<>();
    itemList.add(testItem);

    when(itemRepo.findByHousehold_Id(TEST_HOUSEHOLD_ID)).thenReturn(Optional.of(itemList));

    List<ItemGenericDTO> result = itemService.getItemsByHouseholdId(TEST_HOUSEHOLD_ID, TEST_USER_ID);

    assertEquals(1, result.size());
    assertEquals(testItemDTO.getId(), result.get(0).getId());
  }

  @Test
  @DisplayName("getItemsByHouseholdId should throw IllegalArgumentException if no items for given household exists")
  void getItemsByHouseholdIdNotFound() {
    when(itemRepo.findByHousehold_Id(TEST_HOUSEHOLD_ID)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () ->
        itemService.getItemsByHouseholdId(TEST_HOUSEHOLD_ID, TEST_USER_ID));
  }

  @Test
  @DisplayName("getItemsByHouseholdId should throw IllegalArgumentException if user is not in household")
  void getItemsByHouseholdIdUnauthorized() {
    assertThrows(IllegalArgumentException.class, () ->
        itemService.getItemsByHouseholdId(TEST_HOUSEHOLD_ID, 999));
  }

  @Test
  @DisplayName("getItemsByCategoryId should return list of items with requested category id")
  void getItemsByCategoryIdSuccess() {
    List<Item> itemList = new ArrayList<>();
    itemList.add(testItem);

    when(itemRepo.findByCategory_IdAndHousehold_Id(1, TEST_HOUSEHOLD_ID)).thenReturn(Optional.of(itemList));
    when(categoryRepo.existsById(1)).thenReturn(true);

    List<ItemGenericDTO> result = itemService.getItemsByCategoryIdAndHouseholdId(1, TEST_HOUSEHOLD_ID, TEST_USER_ID);

    assertEquals(1, result.size());
    assertEquals(testItemDTO.getId(), result.get(0).getId());
  }

  @Test
  @DisplayName("getItemsByCategoryId should throw IllegalArgumentException if no items for given category exists")
  void getItemsByCategoryIdNotFound() {
    when(categoryRepo.existsById(1)).thenReturn(true);
    when(itemRepo.findByCategory_IdAndHousehold_Id(1, TEST_HOUSEHOLD_ID)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> itemService.getItemsByCategoryIdAndHouseholdId(1, TEST_HOUSEHOLD_ID, TEST_USER_ID));
  }

  @Test
  @DisplayName("getItemsByCategoryId should throw IllegalArgumentException if category does not exist")
  void getItemsByCategoryIdCategoryBadRequest() {
    when(categoryRepo.existsById(1)).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> itemService.getItemsByCategoryIdAndHouseholdId(1, TEST_HOUSEHOLD_ID, TEST_USER_ID));
  }

  @Test
  @DisplayName("getAllItems should return list of all items")
  void getAllItemsSuccess() {
    List<Item> itemList = new ArrayList<>();
    itemList.add(testItem);

    when(itemRepo.findAll()).thenReturn(itemList);

    List<ItemGenericDTO> result = itemService.getAllItems();

    assertEquals(1, result.size());
    assertEquals(testItemDTO.getId(), result.get(0).getId());
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
    List<Integer> householdIds = Arrays.asList(TEST_HOUSEHOLD_ID);
    ItemCreateRequest itemCreateRequest = new ItemCreateRequest(
        "Melk",
        1.75,
        111,
        222,
        expirationDate,
        householdIds
    );

    Unit mockUnit = new Unit();
    mockUnit.setId(111);
    mockUnit.setEnglishName("Liter");
    mockUnit.setNorwegianName("Liter");

    Category mockCategory = new Category();
    mockCategory.setId(222);
    mockCategory.setEnglishName("Dairy");
    mockCategory.setNorwegianName("Melk");
    mockCategory.setKcalPerUnit(0);
    mockCategory.setIsEssential(false);

    Item savedItem = new Item();
    savedItem.setId(1);
    savedItem.setName("Melk");
    savedItem.setAmount(1.75);
    savedItem.setUnit(mockUnit);
    savedItem.setCategory(mockCategory);
    savedItem.setExpirationDate(expirationDate);
    savedItem.setHousehold(households);

    when(unitRepo.findById(111)).thenReturn(Optional.of(mockUnit));
    when(categoryRepo.findById(222)).thenReturn(Optional.of(mockCategory));
    when(householdRepo.findAllById(householdIds)).thenReturn(households);
    when(itemRepo.save(any(Item.class))).thenReturn(savedItem);

    ItemGenericDTO result = itemService.addItem(itemCreateRequest, TEST_USER_ID);

    assertNotNull(result);
    assertEquals("Melk", result.getName());
    assertEquals(1.75, result.getAmount());
    assertEquals(111, result.getUnitId());
    assertEquals(222, result.getCategoryId());
    assertEquals(expirationDate, result.getExpirationDate());
    assertEquals(1, result.getHouseholdIds().size());
    assertEquals(TEST_HOUSEHOLD_ID, result.getHouseholdIds().get(0));
  }

  @Test
  @DisplayName("AddItem should throw IllegalArgumentException if unit does not exist")
  void addItemUnitNotFound() {
    List<Integer> householdIds = Arrays.asList(TEST_HOUSEHOLD_ID);
    ItemCreateRequest itemCreateRequest = new ItemCreateRequest(
        "Melk",
        1.75,
        111,
        222,
        new Date(),
        householdIds
    );

    when(unitRepo.findById(111)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> itemService.addItem(itemCreateRequest, TEST_USER_ID));
  }

  @Test
  @DisplayName("AddItem should throw IllegalArgumentException if category does not exist")
  void addItemCategoryNotFound() {
    List<Integer> householdIds = Arrays.asList(TEST_HOUSEHOLD_ID);
    ItemCreateRequest itemCreateRequest = new ItemCreateRequest(
        "Melk",
        1.75,
        111,
        222,
        new Date(),
        householdIds
    );

    Unit mockUnit = new Unit();
    mockUnit.setId(111);
    mockUnit.setEnglishName("Liter");

    when(unitRepo.findById(111)).thenReturn(Optional.of(mockUnit));
    when(categoryRepo.findById(222)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> itemService.addItem(itemCreateRequest, TEST_USER_ID));
  }

  @Test
  @DisplayName("AddItem should throw IllegalArgumentException if user not in household")
  void addItemUserNotInHousehold() {
    List<Integer> householdIds = Arrays.asList(TEST_HOUSEHOLD_ID);
    ItemCreateRequest itemCreateRequest = new ItemCreateRequest(
        "Melk",
        1.75,
        111,
        222,
        new Date(),
        householdIds
    );

    assertThrows(IllegalArgumentException.class, () -> itemService.addItem(itemCreateRequest, 999));
  }

  @Test
  @DisplayName("UpdateItem should update an existing item successfully")
  void updateItemSuccess() {
    List<Integer> householdIds = Arrays.asList(TEST_HOUSEHOLD_ID);
    ItemGenericDTO newItemData = new ItemGenericDTO(
        1,
        "Hel Melk",
        1.75,
        111,
        222,
        new Date(),
        householdIds
    );

    Unit mockUnit = new Unit();
    mockUnit.setId(111);
    mockUnit.setEnglishName("Liter");

    Category mockCategory = new Category();
    mockCategory.setId(222);
    mockCategory.setEnglishName("Dairy");
    mockCategory.setKcalPerUnit(0);
    mockCategory.setIsEssential(false);

    Item updatedItem = new Item();
    updatedItem.setId(1);
    updatedItem.setName("Hel Melk");
    updatedItem.setAmount(1.75);
    updatedItem.setUnit(mockUnit);
    updatedItem.setCategory(mockCategory);
    updatedItem.setExpirationDate(new Date());
    updatedItem.setHousehold(households);

    when(itemRepo.findById(1)).thenReturn(Optional.of(testItem));
    when(unitRepo.findById(111)).thenReturn(Optional.of(mockUnit));
    when(categoryRepo.findById(222)).thenReturn(Optional.of(mockCategory));
    when(householdRepo.findAllById(householdIds)).thenReturn(households);
    when(itemRepo.save(any(Item.class))).thenReturn(updatedItem);

    ItemGenericDTO result = itemService.updateItem(newItemData, TEST_USER_ID);

    assertEquals("Hel Melk", result.getName());
    assertEquals(1, result.getHouseholdIds().size());
    assertEquals(TEST_HOUSEHOLD_ID, result.getHouseholdIds().get(0));
  }

  @Test
  @DisplayName("UpdateItem should throw IllegalArgumentException if item does not exist")
  void updateItemNotFound() {
    List<Integer> householdIds = Arrays.asList(TEST_HOUSEHOLD_ID);
    ItemGenericDTO newItemData = new ItemGenericDTO(
        1,
        "Hel Melk",
        1.75,
        111,
        222,
        new Date(),
        householdIds
    );

    when(itemRepo.findById(1)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> itemService.updateItem(newItemData, TEST_USER_ID));
  }

  @Test
  @DisplayName("UpdateItem should throw IllegalArgumentException if user is not authorized")
  void updateItemUnauthorized() {
    List<Integer> householdIds = Arrays.asList(TEST_HOUSEHOLD_ID);
    ItemGenericDTO newItemData = new ItemGenericDTO(
        1,
        "Hel Melk",
        1.75,
        111,
        222,
        new Date(),
        householdIds
    );

    when(itemRepo.findById(1)).thenReturn(Optional.of(testItem));

    assertThrows(IllegalArgumentException.class, () -> itemService.updateItem(newItemData, 999));
  }

  @Test
  @DisplayName("UpdateItem should throw IllegalArgumentException if unit does not exist")
  void updateItemUnitNotFound() {
    List<Integer> householdIds = Arrays.asList(TEST_HOUSEHOLD_ID);
    ItemGenericDTO newItemData = new ItemGenericDTO(
        1,
        "Hel Melk",
        1.75,
        111,
        222,
        new Date(),
        householdIds
    );

    when(itemRepo.findById(1)).thenReturn(Optional.of(testItem));
    when(unitRepo.findById(111)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> itemService.updateItem(newItemData, TEST_USER_ID));
  }

  @Test
  @DisplayName("UpdateItem should throw IllegalArgumentException if category does not exist")
  void updateItemCategoryNotFound() {
    List<Integer> householdIds = Arrays.asList(TEST_HOUSEHOLD_ID);
    ItemGenericDTO newItemData = new ItemGenericDTO(
        1,
        "Hel Melk",
        1.75,
        111,
        222,
        new Date(),
        householdIds
    );

    when(itemRepo.findById(1)).thenReturn(Optional.of(testItem));
    when(unitRepo.findById(111)).thenReturn(Optional.of(new Unit()));
    when(categoryRepo.findById(222)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> itemService.updateItem(newItemData, TEST_USER_ID));
  }

  @Test
  @DisplayName("deleteItem should successfully remove an item")
  void deleteItemSuccess() {
    int itemId = 1;

    when(itemRepo.findById(itemId)).thenReturn(Optional.of(testItem));

    itemService.deleteItem(itemId, TEST_USER_ID);

    verify(itemRepo).delete(testItem);
  }

  @Test
  @DisplayName("DeleteItem should throw IllegalArgumentException if item does not exist")
  void deleteItemNotFound() {
    when(itemRepo.findById(1)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> itemService.deleteItem(1, TEST_USER_ID));
  }

  @Test
  @DisplayName("DeleteItem should throw IllegalArgumentException if user is not authorized")
  void deleteItemUnauthorized() {
    when(itemRepo.findById(1)).thenReturn(Optional.of(testItem));

    assertThrows(IllegalArgumentException.class, () -> itemService.deleteItem(1, 999));
  }
}