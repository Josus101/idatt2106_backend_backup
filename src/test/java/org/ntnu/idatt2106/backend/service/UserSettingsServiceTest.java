package org.ntnu.idatt2106.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;
import org.ntnu.idatt2106.backend.dto.user.UserStoreSettingsRequest;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.model.UserSettings;
import org.ntnu.idatt2106.backend.repo.UserRepo;
import org.ntnu.idatt2106.backend.repo.UserSettingsRepo;
import org.springframework.boot.convert.DataSizeUnit;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserSettingsServiceTest {

  @Mock
  private UserSettingsRepo userSettingsRepo;

  @Mock
  private UserRepo userRepo;

  @InjectMocks
  private UserSettingsService userSettingsService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void saveUserSettings_success() {
    int userId = 1;
    User user = new User();
    user.setId(userId);
    UserStoreSettingsRequest settings = new UserStoreSettingsRequest();
    String settingsJson = "{\"showStorageStatusOnFrontpage\":true,\"showHouseholdStatusOnFrontpage\":false}";

    when(userRepo.findById(userId)).thenReturn(Optional.of(user));
    when(userSettingsRepo.findByUserId(userId)).thenReturn(Optional.empty());

    try (MockedStatic<SettingsUtils> mockedSettingsUtils = mockStatic(SettingsUtils.class)) {
      mockedSettingsUtils.when(() -> SettingsUtils.convertToJson(settings)).thenReturn(settingsJson);

      userSettingsService.saveUserSettings(userId, settings);

      verify(userRepo).findById(userId);
      verify(userSettingsRepo).findByUserId(userId);
      verify(userSettingsRepo).save(any(UserSettings.class));
    }
  }

  @Test
  void getUserSettings_success() {
    int userId = 1;
    String settingsJson = "{\"showStorageStatusOnFrontpage\":true,\"showHouseholdStatusOnFrontpage\":false}";
    UserSettings userSettings = new UserSettings();
    userSettings.setSettings(settingsJson);
    UserStoreSettingsRequest settingsRequest = new UserStoreSettingsRequest();

    when(userSettingsRepo.findByUserId(userId)).thenReturn(Optional.of(userSettings));

    try (MockedStatic<SettingsUtils> mockedSettingsUtils = mockStatic(SettingsUtils.class)) {
      mockedSettingsUtils.when(() -> SettingsUtils.convertFromJson(settingsJson)).thenReturn(settingsRequest);

      UserStoreSettingsRequest result = userSettingsService.getUserSettings(userId);

      assertNotNull(result);
      verify(userSettingsRepo).findByUserId(userId);
    }
  }

  @Test
  void getUserSettings_jsonParsingError_throwsException() {
    int userId = 1;
    String invalidJson = "{invalid: json}";
    UserSettings userSettings = new UserSettings();
    userSettings.setSettings(invalidJson);

    when(userSettingsRepo.findByUserId(userId)).thenReturn(Optional.of(userSettings));

    try (MockedStatic<SettingsUtils> mockedSettingsUtils = mockStatic(SettingsUtils.class)) {
      mockedSettingsUtils.when(() -> SettingsUtils.convertFromJson(invalidJson))
              .thenThrow(new RuntimeException("Error converting JSON to settings"));

      RuntimeException exception = assertThrows(RuntimeException.class, () ->
              userSettingsService.getUserSettings(userId)
      );

      assertEquals("Failed to parse JSON for user ID " + userId, exception.getMessage());
      verify(userSettingsRepo).findByUserId(userId);
    }
  }

  @Test
  @DisplayName("setUserSettings should thrrow IllegalArgumentException when user is not found")
  void testSetUserSettings_UserNotFound() {
    int userId = 1;
    UserStoreSettingsRequest settings = new UserStoreSettingsRequest();
    settings.setShowStorageStatusOnFrontpage(true);
    settings.setShowHouseholdStatusOnFrontpage(false);

    when(userRepo.findById(userId)).thenReturn(Optional.empty());

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            userSettingsService.saveUserSettings(userId, settings)
    );

    assertEquals("User not found with ID: " + userId, exception.getMessage());
  }

  @Test
  @DisplayName("getUserSettings should return null when no settings are found")
  void testGetUserSettings_NoSettingsFound() {
    int userId = 1;

    when(userSettingsRepo.findByUserId(userId)).thenReturn(Optional.empty());

    UserStoreSettingsRequest result = userSettingsService.getUserSettings(userId);

    assertNull(result);
    verify(userSettingsRepo).findByUserId(userId);
  }
}
