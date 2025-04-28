package org.ntnu.idatt2106.backend.repo;

import org.ntnu.idatt2106.backend.model.EmergencyService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmergencyServiceRepo extends JpaRepository<EmergencyService, Integer> {
    Optional<EmergencyService> findByLocalID(String localID);
}
