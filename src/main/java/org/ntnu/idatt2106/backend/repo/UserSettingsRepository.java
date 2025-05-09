package org.ntnu.idatt2106.backend.repo;

import org.ntnu.idatt2106.backend.model.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for accessing user settings data in the database.
 * This interface extends JpaRepository to provide CRUD operations for UserSettings entities.
 */
public interface UserSettingsRepository extends JpaRepository<UserSettings, Integer> {

    /**
     * Finds a UserSettings entity by its user ID.
     *
     * @param userId The ID of the user.
     * @return An Optional containing the UserSettings if found, or empty if not.
     */
  Optional<UserSettings> findByUserId(int userId);
}