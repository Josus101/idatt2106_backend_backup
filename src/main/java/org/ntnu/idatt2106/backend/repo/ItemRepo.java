package org.ntnu.idatt2106.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ntnu.idatt2106.backend.model.Item;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Item entity.
 */
public interface ItemRepo extends JpaRepository<Item, Integer> {
  /**
   * Finds an item by its id.
   * @param id the id of the item
   * @return the item with the given id
   */
  Optional<Item> findById(int id);

  /**
   * Finds an item by its name.
   * @param name the name of the item
   * @return the item with the given name
   */
  Optional<Item> findByName(String name);

  /**
   * Finds all items associated with a given household ID.
   * @param householdId the ID of the household
   * @return a list of items belonging to the given household
   */
  Optional<List<Item>> findByHousehold_Id(int householdId);

  /**
   * Finds all items associated with a given category ID and household ID.
   * @param categoryId the ID of the category
   * @param householdId the ID of the household
   * @return a list of items belonging to the given category and household
   */
  Optional<List<Item>> findByCategory_IdAndHousehold_Id(int categoryId, int householdId);

  /**
   * Finds all items in the repository.
   * @return a {@link List} of all items
   */
  @Override
  List<Item> findAll();

  /**
   * Find items by category.
   * @param id the id of the category
   * @return true if the item is found in the category, false otherwise
   */
  List<Item> findByCategoryId(int id);
}
