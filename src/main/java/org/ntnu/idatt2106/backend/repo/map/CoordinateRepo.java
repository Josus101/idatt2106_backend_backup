package org.ntnu.idatt2106.backend.repo.map;

import org.ntnu.idatt2106.backend.model.map.Coordinate;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for the Coordinate model.
 * This interface extends JpaRepository to provide CRUD operations for the Coordinate entity.
 * It is used to interact with the database and perform operations on the Coordinate table.
 *
 * @author André Merkesdal
 * @version 0.2
 * @since 0.1
 */
public interface CoordinateRepo extends JpaRepository<Coordinate, Long> {
}

