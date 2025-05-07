package org.ntnu.idatt2106.backend.repo.map;

import org.ntnu.idatt2106.backend.model.map.MapZone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for the MapZone model.
 * This interface extends JpaRepository to provide CRUD operations for the MapZone entity.
 * It is used to interact with the database and perform operations on the MapZone table.
 *
 * @author André Merkesdal
 * @since 0.1
 */
public interface MapZoneRepo extends JpaRepository<MapZone, Long> {

  /**
     * Finds a map zone by its id.
     *
     * @param id the id of the map zone
     * @return the map zone with the given id
     */
    Optional<MapZone> findById(Long id);

    /**
     * Finds a map zone by its name.
     *
     * @param name the name of the map zone
     * @return the map zone with the given name
     */
  Optional<MapZone> findByName(String name);
}
