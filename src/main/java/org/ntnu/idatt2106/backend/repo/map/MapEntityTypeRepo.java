package org.ntnu.idatt2106.backend.repo.map;

import org.ntnu.idatt2106.backend.model.map.MapEntityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for a map entity's type.
 */
public interface MapEntityTypeRepo extends JpaRepository<MapEntityType, Integer> {
  /**
   * Finds a type by its id.
   * @param id the id of the type
   * @return the type with the given id
   */
  Optional<MapEntityType> findById(int id);

  /**
   * Finds a type by its name.
   * @param name the name of the type
   * @return the type with the given name
   */
  Optional<MapEntityType> findByName(String name);
}
