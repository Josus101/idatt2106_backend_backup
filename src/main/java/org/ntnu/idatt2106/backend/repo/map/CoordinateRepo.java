package org.ntnu.idatt2106.backend.repo.map;

import org.ntnu.idatt2106.backend.model.map.Coordinate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoordinateRepo extends JpaRepository<Coordinate, Long> {
}

