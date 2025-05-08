package org.ntnu.idatt2106.backend.repo.map;

import org.ntnu.idatt2106.backend.model.map.MapMarkerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MapMarkerTypeRepo extends JpaRepository<MapMarkerType, Integer> {
  /**
   * Finds a type by its id.
   * @param id the id of the type
   * @return the type with the given id
   */
  Optional<MapMarkerType> findById(int id);

  /**
   * Finds a type by its name.
   * @param name the name of the type
   * @return the type with the given name
   */
  Optional<MapMarkerType> findByName(String name);
}
