package org.ntnu.idatt2106.backend.repo;

import org.ntnu.idatt2106.backend.model.EmergencyService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for EmergencyService entity.
 */
public interface EmergencyServiceRepo extends JpaRepository<EmergencyService, Integer> {

    /**
     * Finds an emergency service by its local ID.
     * @param localID the local ID of the emergency service
     * @return the emergency service with the given local ID
     */
    Optional<EmergencyService> findByLocalID(String localID);
}
