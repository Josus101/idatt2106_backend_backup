package org.ntnu.idatt2106.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ntnu.idatt2106.backend.dto.user.UserStoreSettingsRequest;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SettingsUtilsTest {

  private ObjectMapper mockObjectMapper;

  @Test
  void convertToJson_success() {
    UserStoreSettingsRequest settings = new UserStoreSettingsRequest();
    settings.setShowStorageStatusOnFrontpage(true);
    settings.setShowHouseholdStatusOnFrontpage(false);

    String json = SettingsUtils.convertToJson(settings);
    assertNotNull(json);
    assertTrue(json.contains("showStorageStatusOnFrontpage"));
    assertTrue(json.contains("showHouseholdStatusOnFrontpage"));
  }

  @Test
  void convertToJson_failure() {
    UserStoreSettingsRequest settings = null;

    RuntimeException exception = assertThrows(RuntimeException.class, () ->
            SettingsUtils.convertToJson(settings)
    );

    assertEquals("Error: Provided settings object is null", exception.getMessage());
  }

//  @Test
//  void testConvertToJson_JsonProcessingException() throws NoSuchFieldException, IllegalAccessException {
//    // Arrange
//    UserStoreSettingsRequest settings = new UserStoreSettingsRequest();
//    ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
//
//    try {
//      when(mockObjectMapper.writeValueAsString(settings)).thenThrow(new JsonProcessingException("Mock exception") {});
//    } catch (JsonProcessingException e) {
//      fail("Mocking JsonProcessingException failed");
//    }
//
//    // Use reflection to set the private static final field
//    Field objectMapperField = SettingsUtils.class.getDeclaredField("objectMapper");
//    objectMapperField.setAccessible(true);
//    Object originalObjectMapper = objectMapperField.get(null);
//
//    try {
//      // Temporarily replace the static field with the mock
//      objectMapperField.set(null, mockObjectMapper);
//
//      // Act & Assert
//      RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//        SettingsUtils.convertToJson(settings);
//      });
//
//      assertTrue(exception.getMessage().startsWith("Error:"));
//      assertTrue(exception.getCause() instanceof JsonProcessingException);
//    } finally {
//      // Restore the original ObjectMapper after the test
//      objectMapperField.set(null, originalObjectMapper);
//    }
//  }

  @Test
  void convertFromJson_success() {
    String json = "{\"showStorageStatusOnFrontpage\":true,\"showHouseholdStatusOnFrontpage\":false}";

    UserStoreSettingsRequest settings = SettingsUtils.convertFromJson(json);
    assertNotNull(settings);
    assertTrue(settings.isShowStorageStatusOnFrontpage());
    assertFalse(settings.isShowHouseholdStatusOnFrontpage());
  }

  @Test
  void convertFromJson_failure() {
    String invalidJson = "{invalid: json}";

    RuntimeException exception = assertThrows(RuntimeException.class, () ->
            SettingsUtils.convertFromJson(invalidJson)
    );

    assertTrue(exception.getMessage().contains("Error converting JSON to settings"));
  }
}
