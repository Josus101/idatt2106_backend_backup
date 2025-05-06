package org.ntnu.idatt2106.backend.repo.map;

import org.ntnu.idatt2106.backend.model.map.CoordinatePolygon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoordinatePolygonRepo extends JpaRepository<CoordinatePolygon, Long> {
}
