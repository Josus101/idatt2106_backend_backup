package org.ntnu.idatt2106.backend.service;

import org.ntnu.idatt2106.backend.dto.user.UserStoreSettingsRequest;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.model.UserSettings;
import org.ntnu.idatt2106.backend.repo.UserRepo;
import org.ntnu.idatt2106.backend.repo.UserSettingsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for managing user settings.
 * This class provides methods to save and retrieve user settings.
 *
 * @Author Jonas Reiher
 * @since 0.1
 */
@Service
public class UserSettingsService {

  private final UserSettingsRepo userSettingsRepo;

  private final UserRepo userRepo;

  /**
   * Constructor for UserSettingsService.
   *
   * @param userSettingsRepo The UserSettingsRepo to be used for database operations.
   *
   */
  @Autowired
  public UserSettingsService(UserSettingsRepo userSettingsRepo, UserRepo userRepo) {
    this.userSettingsRepo = userSettingsRepo;
    this.userRepo = userRepo;
  }

  /**
   * Saves user settings to the database.
   *
   * @param userId The ID of the user.
   * @param settings The UserStoreSettingsRequest object containing the settings to be saved.
   */
  public void saveUserSettings(int userId, UserStoreSettingsRequest settings) {
    String settingsJson = SettingsUtils.convertToJson(settings);

    User user = userRepo.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

    UserSettings userSettings = userSettingsRepo.findByUserId(userId).orElse(new UserSettings());
    userSettings.setUser(user); // viktig: setter JPA-managed User
    userSettings.setSettings(settingsJson);

    userSettingsRepo.save(userSettings);
  }


  /**
   * Retrieves user settings from the database.
   *
   * @param userId The ID of the user.
   * @return The UserStoreSettingsRequest object containing the user's settings, or null if not found.
   */
  public UserStoreSettingsRequest getUserSettings(int userId) throws RuntimeException {
    Optional<UserSettings> optionalSettings = userSettingsRepo.findByUserId(userId);

    if (optionalSettings.isEmpty()) {
      System.out.println("No settings found for user ID: " + userId);
      return null;
    }

    String json = optionalSettings.get().getSettings();

    try {
      return SettingsUtils.convertFromJson(json);
    } catch (Exception e) {
      System.out.println("Failed to parse JSON for user ID " + userId + ": " + json);
      throw new RuntimeException("Failed to parse JSON for user ID " + userId, e);
    }
  }
}
