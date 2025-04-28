package org.ntnu.idatt2106.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * This class is used to configure the application.
 */
@Configuration
public class AppConfig {

  /**
   * This method creates a RestTemplate bean that can be used to make HTTP requests.
   * @return a RestTemplate object
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
