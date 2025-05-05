package org.ntnu.idatt2106.backend.repo.map;

import org.ntnu.idatt2106.backend.model.map.MapZone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

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
