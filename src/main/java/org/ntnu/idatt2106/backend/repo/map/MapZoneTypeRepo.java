package org.ntnu.idatt2106.backend.repo.map;

import java.util.Optional;

import org.ntnu.idatt2106.backend.model.map.MapZoneType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for type entity.
 */
public interface MapZoneTypeRepo extends JpaRepository<MapZoneType, Integer> {
  /**
   * Finds a type by its id.
    * @param id the id of the type
   * @return the type with the given id
   */
  Optional<MapZoneType> findById(int id);

  /**
   * Finds a type by its name.
   * @param name the name of the type
   * @return the type with the given name
   */
  Optional<MapZoneType> findByName(String name);
}
