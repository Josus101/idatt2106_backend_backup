package org.ntnu.idatt2106.backend.repo.map;

import org.ntnu.idatt2106.backend.model.map.MapEntity;
import org.ntnu.idatt2106.backend.model.map.MapEntityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for map entity.
 *
 * @author André Merkesdal
 * @version 0.2
 * @since 0.1
 */
public interface MapEntityRepo extends JpaRepository<MapEntity, Long> {
  /**
   * Finds a map entity by its id.
   *
   * @param id the id of the map entity
   * @return the map entity with the given id
   */
  MapEntity findById(long id);

  /**
   * Finds a map entity by its name.
   *
   * @param name the name of the map entity
   * @return the map entity with the given name
   */
  MapEntity findByName(String name);

  /**
   * Finds a map entity by its local id.
   */
  MapEntity findByLocalID(String localID);

  /**
   * Finds map entities by its type.
   *
   * @param name the name of the map entity type
   * @return the list of map entities with the given type
   */
  List<MapEntity> findAllByMapEntityType_Name(String name);

}
