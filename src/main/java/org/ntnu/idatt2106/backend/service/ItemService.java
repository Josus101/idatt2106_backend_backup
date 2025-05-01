package org.ntnu.idatt2106.backend.service;

import jakarta.persistence.EntityNotFoundException;
import org.ntnu.idatt2106.backend.dto.item.ItemCreateRequest;
import org.ntnu.idatt2106.backend.dto.item.ItemGenericDTO;
import org.ntnu.idatt2106.backend.model.Category;
import org.ntnu.idatt2106.backend.model.Item;
import org.ntnu.idatt2106.backend.model.Unit;
import org.ntnu.idatt2106.backend.repo.CategoryRepo;
import org.ntnu.idatt2106.backend.repo.ItemRepo;
import org.ntnu.idatt2106.backend.repo.UnitRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Service class for handling item-related operations.
 * This class is responsible for the business logic related to items.
 *
 * @author Jonas Reiher
 * @since 0.1
 * @version 0.1
 */
@Service
public class ItemService {

  // Repositories
  @Autowired
  ItemRepo itemRepo;

  @Autowired
  CategoryRepo categoryRepo;

  @Autowired
  UnitRepo unitRepo;

  /**
   * Retrieves an item by its ID.
   *
   * @param id the ID of the item
   * @return the item with the given ID
   */
  public ItemGenericDTO getItemById(int id) {
    return itemRepo.findById(id)
            .map(item -> new ItemGenericDTO(
                    item.getId(),
                    item.getName(),
                    item.getAmount(),
                    item.getUnit().getId(),
                    item.getCategory().getId(),
                    item.getExpirationDate()))
            .orElseThrow(() -> new EntityNotFoundException("Item not found"));
  }

  /**
   * Retrieves all items associated with a household
   * @param id the ID of the household
   * @return a list of items associated with a household
   */
  public List<ItemGenericDTO> getItemsByHouseholdId(int id) {
    // TODO: implement check for household ID (e.g.)

    List<Item> items = itemRepo.findByHousehold_Id(id)
            .orElseThrow(() -> new EntityNotFoundException("No items found for this household"));

    return items.stream().map(item -> new ItemGenericDTO(
            item.getId(),
            item.getName(),
            item.getAmount(),
            item.getUnit().getId(),
            item.getCategory().getId(),
            item.getExpirationDate()
    )).toList();
  }

  /**
   * Retrieves all items associated with a category
   * @param id the ID of the category
   * @return a list of items associated with a category
   */
  public List<ItemGenericDTO> getItemsByCategoryId(int id) {
    if (!categoryRepo.existsById(id)) {
      throw new IllegalArgumentException("Category does not exist");
    }

    List<Item> items = itemRepo.findByCategory_Id(id)
            .orElseThrow(() -> new EntityNotFoundException("No items found for this category"));

    return items.stream().map(item -> new ItemGenericDTO(
            item.getId(),
            item.getName(),
            item.getAmount(),
            item.getUnit().getId(),
            item.getCategory().getId(),
            item.getExpirationDate()
    )).toList();
  }

  /**
   * Retrieves all items in the database
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
            item.getExpirationDate()
    )).toList();
  }

  /**
   * Adds a new item to the database.
   * @param itemCreateRequest the data of the item to be added
   */
  @Transactional
  public ItemGenericDTO addItem(ItemCreateRequest itemCreateRequest) throws EntityNotFoundException {
    Item item = new Item();

    item.setName(itemCreateRequest.getName());
    item.setAmount(itemCreateRequest.getAmount());

    Unit unit = unitRepo.findById(itemCreateRequest.getUnitId())
            .orElseThrow(() -> new EntityNotFoundException("Unit not found"));
    item.setUnit(unit);

    Category category = categoryRepo.findById(itemCreateRequest.getCategoryId())
            .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    item.setCategory(category);


    item.setExpirationDate(itemCreateRequest.getExpirationDate());

    // save the item
    itemRepo.save(item);

    return new ItemGenericDTO(
            item.getId(),
            item.getName(),
            item.getAmount(),
            item.getUnit().getId(),
            item.getCategory().getId(),
            item.getExpirationDate());
  }

  /**
   * Updates an existing item in the database.
   * @param itemData the data of the item to be updated
   */
  @Transactional
  public ItemGenericDTO updateItem(ItemGenericDTO itemData) {
    Item item = itemRepo.findById(itemData.getId())
            .orElseThrow(() -> new EntityNotFoundException("Item not found"));

    item.setName(itemData.getName());
    item.setAmount(itemData.getAmount());

    Unit unit = unitRepo.findById(itemData.getUnitId())
            .orElseThrow(() -> new EntityNotFoundException("Unit not found"));
    item.setUnit(unit);

    Category category = categoryRepo.findById(itemData.getCategoryId())
            .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    item.setCategory(category);


    item.setExpirationDate(itemData.getExpirationDate());

    // save the item
    itemRepo.save(item);

    return new ItemGenericDTO(
            item.getId(),
            item.getName(),
            item.getAmount(),
            item.getUnit().getId(),
            item.getCategory().getId(),
            item.getExpirationDate()
    );
  }

  /**
   * Deletes an item from the database.
   * @param id the ID of the item to be deleted
   */
  public void deleteItem(int id) {
    Item item = itemRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Item not found"));

    item.getHousehold().clear();
    itemRepo.save(item);

    itemRepo.delete(item);
  }

}
