package org.ntnu.idatt2106.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for handling Spring Security.
 * This configuration allows access to Swagger endpoints.
 */
@Configuration
public class SecurityConfig {

  /**
   * Configures the security filter chain for allowing Swagger endpoints.
   *
   * @param http the {@link HttpSecurity} object to configure
   * @return the configured {@link SecurityFilterChain}
   * @throws Exception if an error occurs during configuration
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .headers(headers -> headers.frameOptions(frame -> frame.disable())) // tillat H2 console i iframe
            .cors(cors->{})
            .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/api-docs/**",
                "/swagger-ui.html",
                "/swagger-resources/**",
                "/webjars/**",
                "/h2-console/**",
                "/api/email/test",
                "/api/email/verify/**",
                "/api/email/reset-password/**",
                "/api/users/reset-password/**",
                "/api/users/verify/**",
                "/api/users/is-auth/**",
                "/api/households/**") // TODO: ONLY FOR TESTING REMOVE LATER
            .permitAll()
            .anyRequest().permitAll() //TODO: switch to authenticated users only
        );

    return http.build();
  }
}