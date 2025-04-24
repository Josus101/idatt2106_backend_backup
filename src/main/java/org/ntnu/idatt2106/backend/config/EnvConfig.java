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
    Dotenv dotenv = Dotenv.configure().load();
    dotenv.entries().forEach(entry ->
        System.setProperty(entry.getKey(), entry.getValue())
    );
  }
}