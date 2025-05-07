package org.ntnu.idatt2106.backend.repo.map;

import org.ntnu.idatt2106.backend.model.map.CoordinatePolygon;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for the CoordinatePolygon model.
 * This interface extends JpaRepository to provide CRUD operations for the CoordinatePolygon entity.
 * It is used to interact with the database and perform operations on the CoordinatePolygon table.
 *
 * @author André Merkesdal
 * @since 0.1
 */
public interface CoordinatePolygonRepo extends JpaRepository<CoordinatePolygon, Long> {
}
