package org.ntnu.idatt2106.backend.service;

import org.ntnu.idatt2106.backend.dto.user.UserStoreSettingsRequest;
import org.ntnu.idatt2106.backend.model.UserSettings;
import org.ntnu.idatt2106.backend.repo.UserSettingsRepository;
import org.springframework.stereotype.Service;

/**
 * Service class for managing user settings.
 * This class provides methods to save and retrieve user settings.
 *
 * @Author Jonas Reiher
 * @since 0.1
 */
@Service
public class UserSettingsService {

  private final UserSettingsRepository repository;

  /**
   * Constructor for UserSettingsService.
   *
   * @param repository The UserSettingsRepository to be used for database operations.
   */
  public UserSettingsService(UserSettingsRepository repository) {
    this.repository = repository;
  }

  /**
   * Saves user settings to the database.
   *
   * @param userId The ID of the user.
   * @param settings The UserStoreSettingsRequest object containing the settings to be saved.
   */
  public void saveUserSettings(int userId, UserStoreSettingsRequest settings) {
    String settingsJson = SettingsUtils.convertToJson(settings);
    UserSettings userSettings = new UserSettings(userId, settingsJson);
    repository.save(userSettings);
  }

  /**
   * Retrieves user settings from the database.
   *
   * @param userId The ID of the user.
   * @return The UserStoreSettingsRequest object containing the user's settings, or null if not found.
   */
  public UserStoreSettingsRequest getUserSettings(int userId) {
    return repository.findByUserId(userId)
            .map(userSettings -> SettingsUtils.convertFromJson(userSettings.getSettings()))
            .orElse(null);
  }
}
