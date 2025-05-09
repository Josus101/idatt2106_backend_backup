package org.ntnu.idatt2106.backend.repo;

import org.ntnu.idatt2106.backend.model.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for Unit entity.
 */
public interface UnitRepo extends JpaRepository<Unit, Integer> {
  /**
   * Finds a unit by its id.
    * @param id the id of the unit
   * @return the unit with the given id
   */
  Optional<Unit> findById(int id);

  /**
   * Finds a unit by its name.
   * @param name the name of the unit
   * @return the unit with the given name
   */
  Optional<Unit> findByEnglishName(String name);
}
