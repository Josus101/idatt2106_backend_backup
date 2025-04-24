package org.ntnu.idatt2106.backend.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(classes = AppConfig.class)
class AppConfigTest {

  @Autowired
  private RestTemplate restTemplate;

  @Test
  @DisplayName("Test that RestTemplate bean is created")
  void testRestTemplateBeanIsCreated() {
    assertNotNull(restTemplate, "RestTemplate bean should not be null");
  }
}
