package org.ntnu.idatt2106.backend.repo.map;

import org.ntnu.idatt2106.backend.model.map.MapMarker;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for the MapMarker model.
 * This interface extends JpaRepository to provide CRUD operations for the MapMarker entity.
 * It is used to interact with the database and perform operations on the MapMarker table.
 *
 * @author André Merkesdal
 * @since 0.1
 */
public interface MapMarkerRepo extends JpaRepository<MapMarker, Long> {
}
