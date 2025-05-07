package org.ntnu.idatt2106.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ntnu.idatt2106.backend.dto.user.UserStoreSettingsRequest;

/**
 * Utility class for converting UserStoreSettingsRequest to and from JSON.
 * This class uses Jackson ObjectMapper for JSON serialization and deserialization.
 *
 * @Author Jonas Reiher
 * @since 0.1
 */
public class SettingsUtils {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Converts a UserStoreSettingsRequest object to its JSON representation.
   *
   * @param settings The UserStoreSettingsRequest object to convert.
   * @return The JSON string representation of the settings.
   */
  public static String convertToJson(UserStoreSettingsRequest settings) {
    try {
      return objectMapper.writeValueAsString(settings);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error converting settings to JSON", e);
    }
  }

  /**
   * Converts a JSON string to a UserStoreSettingsRequest object.
   *
   * @param json The JSON string to convert.
   * @return The UserStoreSettingsRequest object.
   */
  public static UserStoreSettingsRequest convertFromJson(String json) {
    try {
      return objectMapper.readValue(json, UserStoreSettingsRequest.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error converting JSON to settings", e);
    }
  }
}
