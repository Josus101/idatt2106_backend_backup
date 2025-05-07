package org.ntnu.idatt2106.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.ntnu.idatt2106.backend.dto.item.ItemCreateRequest;
import org.ntnu.idatt2106.backend.dto.item.ItemGenericDTO;
import org.ntnu.idatt2106.backend.model.Category;
import org.ntnu.idatt2106.backend.model.Household;
import org.ntnu.idatt2106.backend.model.Item;
import org.ntnu.idatt2106.backend.model.Unit;
import org.ntnu.idatt2106.backend.repo.CategoryRepo;
import org.ntnu.idatt2106.backend.repo.HouseholdRepo;
import org.ntnu.idatt2106.backend.repo.ItemRepo;
import org.ntnu.idatt2106.backend.repo.UnitRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for handling item-related operations. This class is responsible for the business
 * logic related to items.
 *
 * @version 0.2
 * @since 0.1
 * @author Jonas Reiher, Eskild Smestu
 */
@Service
public class ItemService {

  private final ItemRepo itemRepo;
  private final CategoryRepo categoryRepo;
  private final UnitRepo unitRepo;
  private final HouseholdRepo householdRepo;

  /**
   * Constructor injection for repositories
   *
   * @param itemRepo      Repository for items
   * @param categoryRepo  Repository for categories
   * @param unitRepo      Repository for units
   * @param householdRepo Repository for households
   */
  @Autowired
  public ItemService(ItemRepo itemRepo, CategoryRepo categoryRepo, UnitRepo unitRepo,
      HouseholdRepo householdRepo) {
    this.itemRepo = itemRepo;
    this.categoryRepo = categoryRepo;
    this.unitRepo = unitRepo;
    this.householdRepo = householdRepo;
  }

  /**
   * Retrieves an item by its ID.
   *
   * @param id     the ID of the item
   * @param userId the ID of the user requesting the item
   * @return the item with the given ID
   * @throws IllegalArgumentException if the item is not found
   * @throws IllegalArgumentException if the user is not authorized
   */
  public ItemGenericDTO getItemById(int id, int userId) {
    Item item = itemRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Item not found"));

    validateOperation(userId, id);

    return new ItemGenericDTO(
        item.getId(),
        item.getName(),
        item.getAmount(),
        item.getUnit().getId(),
        item.getCategory().getId(),
        item.getExpirationDate(),
        getIdsByHousehold(item.getHousehold()));
  }

  /**
   * Retrieves all items associated with a household
   *
   * @param id     the ID of the household
   * @param userId the ID of the user requesting the items
   * @return a list of items associated with a household
   * @throws IllegalArgumentException if no items are found
   * @throws IllegalArgumentException if the user is not authorized
   */
  public List<ItemGenericDTO> getItemsByHouseholdId(int id, int userId) {
    isUserInHousehold(userId, id);

    List<Item> items = itemRepo.findByHousehold_Id(id)
        .orElseThrow(() -> new IllegalArgumentException("No items found for this household"));

    return items.stream().map(item -> new ItemGenericDTO(
        item.getId(),
        item.getName(),
        item.getAmount(),
        item.getUnit().getId(),
        item.getCategory().getId(),
        item.getExpirationDate(),
        getIdsByHousehold(item.getHousehold())
    )).collect(Collectors.toList());
  }

  /**
   * Retrieves all items associated with a category and household
   *
   * @param id     the ID of the category
   * @param userId the ID of the user requesting the items
   * @param houseHoldId the ID of the household
   * @return a list of items associated with a category
   * @throws IllegalArgumentException if the category does not exist
   * @throws IllegalArgumentException if no items are found
   */
  public List<ItemGenericDTO> getItemsByCategoryIdAndHouseholdId(int id, int houseHoldId, int userId) {
    if (!categoryRepo.existsById(id)) {
      throw new IllegalArgumentException("Category does not exist");
    }

    List<Item> items = itemRepo.findByCategory_IdAndHousehold_Id(id, houseHoldId)
        .orElseThrow(() -> new IllegalArgumentException("No items found for this category"));

    return items.stream()
        .filter(item -> isUserAuthorizedForItem(userId, item))
        .map(item -> new ItemGenericDTO(
            item.getId(),
            item.getName(),
            item.getAmount(),
            item.getUnit().getId(),
            item.getCategory().getId(),
            item.getExpirationDate(),
            getIdsByHousehold(item.getHousehold())
        )).collect(Collectors.toList());
  }

  /**
   * Retrieves all items in the database
   *
   * @return a list of all items
   */
  public List<ItemGenericDTO> getAllItems() {
    List<Item> items = itemRepo.findAll();

    return items.stream().map(item -> new ItemGenericDTO(
        item.getId(),
        item.getName(),
        item.getAmount(),
        item.getUnit().getId(),
        item.getCategory().getId(),
        item.getExpirationDate(),
        getIdsByHousehold(item.getHousehold())
    )).collect(Collectors.toList());
  }

  /**
   * Adds a new item to the database.
   *
   * @param itemCreateRequest the data of the item to be added
   * @param userId the ID of the user adding the item
   * @return the created item
   * @throws IllegalArgumentException if any referenced entity is not found
   * @throws IllegalArgumentException if the user is not authorized
   */
  public ItemGenericDTO addItem(ItemCreateRequest itemCreateRequest, int userId)
      throws IllegalArgumentException {
    Item item = new Item();

    item.setName(itemCreateRequest.getName());
    item.setAmount(itemCreateRequest.getAmount());

    Unit unit = unitRepo.findById(itemCreateRequest.getUnitId())
        .orElseThrow(() -> new IllegalArgumentException("Unit not found"));
    item.setUnit(unit);

    Category category = categoryRepo.findById(itemCreateRequest.getCategoryId())
        .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    item.setCategory(category);

    item.setExpirationDate(itemCreateRequest.getExpirationDate());

    for (Integer householdId : itemCreateRequest.getHouseholdIds()) {
      isUserInHousehold(userId, householdId);
    }

    List<Household> households = getHouseholdsByIds(itemCreateRequest.getHouseholdIds());
    item.setHousehold(households);

    Item savedItem = itemRepo.save(item);

    List<Integer> responseHouseholdIds = itemCreateRequest.getHouseholdIds() != null ?
        itemCreateRequest.getHouseholdIds() : new ArrayList<>();

    return new ItemGenericDTO(
        savedItem.getId(),
        savedItem.getName(),
        savedItem.getAmount(),
        savedItem.getUnit().getId(),
        savedItem.getCategory().getId(),
        savedItem.getExpirationDate(),
        responseHouseholdIds);
  }

  /**
   * Updates an existing item in the database.
   *
   * @param itemData the data of the item to be updated
   * @param userId   the ID of the user updating the item
   * @return the updated item
   * @throws IllegalArgumentException if any referenced entity is not found
   * @throws IllegalArgumentException if the user is not authorized
   */
  public ItemGenericDTO updateItem(ItemGenericDTO itemData, int userId)
      throws IllegalArgumentException {
    Item item = itemRepo.findById(itemData.getId())
        .orElseThrow(() -> new IllegalArgumentException("Item not found"));

    validateOperation(userId, itemData.getId());

    item.setName(itemData.getName());
    item.setAmount(itemData.getAmount());

    Unit unit = unitRepo.findById(itemData.getUnitId())
        .orElseThrow(() -> new IllegalArgumentException("Unit not found"));
    item.setUnit(unit);

    Category category = categoryRepo.findById(itemData.getCategoryId())
        .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    item.setCategory(category);

    item.setExpirationDate(itemData.getExpirationDate());

    if (itemData.getHouseholdIds() != null) {
      for (Integer householdId : itemData.getHouseholdIds()) {
        isUserInHousehold(userId, householdId);
      }

      List<Household> households = getHouseholdsByIds(itemData.getHouseholdIds());
      item.setHousehold(households);
    }

    Item savedItem = itemRepo.save(item);

    return new ItemGenericDTO(
        savedItem.getId(),
        savedItem.getName(),
        savedItem.getAmount(),
        savedItem.getUnit().getId(),
        savedItem.getCategory().getId(),
        savedItem.getExpirationDate(),
        getIdsByHousehold(savedItem.getHousehold())
    );
  }

  /**
   * Deletes an item from the database.
   *
   * @param id     the ID of the item to be deleted
   * @param userId the ID of the user deleting the item
   * @throws IllegalArgumentException if the item is not found
   * @throws IllegalArgumentException if the user is not authorized
   */
  public void deleteItem(int id, int userId) throws IllegalArgumentException {
    Item item = itemRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Item not found"));

    validateOperation(userId, id);

    item.getHousehold().clear();
    itemRepo.save(item);

    itemRepo.delete(item);
  }

  /**
   * Get householdIds from households
   *
   * @param households the households to get IDs from
   * @return a list of household IDs
   * @throws IllegalArgumentException if any household is not found
   */
  private List<Integer> getIdsByHousehold(List<Household> households) {
    if (households == null || households.isEmpty()) {
      throw new IllegalArgumentException("No households found");
    }

    return households.stream()
        .map(Household::getId)
        .collect(Collectors.toList());
  }

  /**
   * Get households from ids
   *
   * @param householdIds the IDs of the households
   * @return a list of households
   * @throws IllegalArgumentException if any household is not found
   */
  private List<Household> getHouseholdsByIds(List<Integer> householdIds) {
    List<Household> households = householdRepo.findAllById(householdIds);

    if (households.size() != householdIds.size()) {
      throw new IllegalArgumentException("One or more households not found");
    }

    return households;
  }

  /**
   * Validates if the user is authorized to perform operations on the item
   *
   * @param userId the ID of the user
   * @param itemId the ID of the item
   * @throws IllegalArgumentException if the user is not authorized
   * @throws IllegalArgumentException  if the item is not found
   */
  private void validateOperation(int userId, int itemId) {
    Item item = itemRepo.findById(itemId)
        .orElseThrow(() -> new IllegalArgumentException("Item not found"));

    if (!isUserAuthorizedForItem(userId, item)) {
      throw new IllegalArgumentException(
          "User is not authorized to perform operations on this item");
    }
  }

  /**
   * Checks if the user is authorized to access an item
   *
   * @param userId the ID of the user
   * @param item   the item to check
   * @return true if the user is authorized, false otherwise
   */
  private boolean isUserAuthorizedForItem(int userId, Item item) {
    List<Household> itemHouseholds = item.getHousehold();

    if (itemHouseholds == null || itemHouseholds.isEmpty()) {
      return false;
    }

    return itemHouseholds.stream()
        .anyMatch(household -> {
          try {
            isUserInHousehold(userId, household.getId());
            return true;
          } catch (IllegalArgumentException e) {
            return false;
          }
        });
  }

  /**
   * Checks if the item is in the household
   *
   * @param item        the item to check
   * @param householdId the ID of the household
   * @throws IllegalArgumentException if the item is not in the household
   */
  private void validateItemInHousehold(Item item, int householdId) {
    boolean isInHousehold = item.getHousehold().stream()
        .anyMatch(household -> household.getId() == householdId);

    if (!isInHousehold) {
      throw new IllegalArgumentException("Item is not in the specified household");
    }
  }

  /**
   * Checks if the user is in the household
   *
   * @param userId      the ID of the user
   * @param householdId the ID of the household
   * @throws IllegalArgumentException if the user is not in the household
   * @throws NoSuchElementException   if the household is not found
   */
  private void isUserInHousehold(int userId, int householdId) {
    Household household = householdRepo.findById(householdId)
        .orElseThrow(() -> new NoSuchElementException("Household not found"));

    boolean isInHousehold = household.getMembers().stream()
        .anyMatch(member -> member.getUser().getId() == userId);

    if (!isInHousehold) {
      throw new IllegalArgumentException("User is not a member of the specified household");
    }
  }
}