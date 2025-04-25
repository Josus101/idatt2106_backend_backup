package org.ntnu.idatt2106.backend.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CorsConfigTest {

  @Autowired
  private MockMvc mockMvc;

  @LocalServerPort
  private int port;

  @Test
  @DisplayName("Should allow CORS requests from allowed origin")
  void testCorsAllowedOrigin() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.options("/some-endpoint")
            .header("Origin", "http://localhost:5173")
            .header("Access-Control-Request-Method", "GET"))
            .andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
            .andExpect(header().string("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS"));
  }

  @Test
  @DisplayName("Should block CORS requests from disallowed origin")
  void testCorsDisallowedOrigin() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.options("/some-endpoint")
            .header("Origin", "http://unauthorized-origin.com")
            .header("Access-Control-Request-Method", "GET"))
            .andExpect(status().isForbidden())
            .andExpect(header().doesNotExist("Access-Control-Allow-Origin"))
            .andExpect(header().doesNotExist("Access-Control-Allow-Methods"))
            .andExpect(header().doesNotExist("Access-Control-Allow-Headers"));
  }
}
