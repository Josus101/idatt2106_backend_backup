package org.ntnu.idatt2106.backend.repo;

import org.ntnu.idatt2106.backend.model.Household;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for accessing household data in the database.
 */
public interface HouseholdRepo extends JpaRepository<Household, Integer> {

    /**
     * Finds a household by its ID.
     *
     * @param id The ID of the household.
     * @return An Optional containing the household if found, or empty if not.
     */
    Optional<Household> findById(int id);

    /**
     * Finds a household by its name.
     *
     * @param name The name of the household.
     * @return An Optional containing the household if found, or empty if not.
     */
    Optional<Household> findByName(String name);
}