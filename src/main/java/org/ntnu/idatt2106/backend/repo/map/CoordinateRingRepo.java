package org.ntnu.idatt2106.backend.repo.map;

import org.ntnu.idatt2106.backend.model.map.CoordinateRing;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for the CoordinateRing model.
 * This interface extends JpaRepository to provide CRUD operations for the CoordinateRing entity.
 * It is used to interact with the database and perform operations on the CoordinateRing table.
 *
 * @author André Merkesdal
 * @since 0.1
 */
public interface CoordinateRingRepo extends JpaRepository<CoordinateRing, Long> {
}
