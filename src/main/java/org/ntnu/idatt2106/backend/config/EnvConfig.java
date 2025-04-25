package org.ntnu.idatt2106.backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for loading environment variables using Dotenv.
 */
@Configuration
public class EnvConfig {

  @PostConstruct
  public void init() {
    try {
      Dotenv dotenv = Dotenv.configure()
          .ignoreIfMalformed()
          .ignoreIfMissing() // Ignore env file if missing, mainly for pipeline
          .load();

      dotenv.entries().forEach(entry ->
          System.setProperty(entry.getKey(), entry.getValue())
      );
    } catch (Exception e) {
      System.out.println("Dotenv load skipped or failed: " + e.getMessage());
    }
  }
}