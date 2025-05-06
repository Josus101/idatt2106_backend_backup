package org.ntnu.idatt2106.backend.repo.map;

import org.ntnu.idatt2106.backend.model.map.MapMarker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MapMarkerRepo extends JpaRepository<MapMarker, Long> {
}
