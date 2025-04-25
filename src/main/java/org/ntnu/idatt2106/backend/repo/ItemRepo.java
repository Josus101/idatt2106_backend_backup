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
   * Finds all items associated with a given category ID.
   * @param categoryId the ID of the category
   * @return a list of items belonging to the given category
   */
  Optional<List<Item>> findByCategory_Id(int categoryId);
}
