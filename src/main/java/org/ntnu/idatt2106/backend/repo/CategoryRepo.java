package org.ntnu.idatt2106.backend.repo;

import org.ntnu.idatt2106.backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for Category entity.
 */
public interface CategoryRepo extends JpaRepository<Category, Integer> {
  /**
   * Finds a category by its id.
   * @param id the id of the category
   * @return the category with the given id
   */
  Optional<Category> findById(int id);

  /**
   * Finds a category by its name.
   * @param name the name of the category
   * @return the category with the given name
   */
  Optional<Category> findByEnglishName(String name);

  /**
   * Finds a category by its Norwegian name.
   * @param norwegianName the Norwegian name of the category
   * @return the category with the given Norwegian name
   */
  Optional<Object> findByNorwegianName(String norwegianName);
}
