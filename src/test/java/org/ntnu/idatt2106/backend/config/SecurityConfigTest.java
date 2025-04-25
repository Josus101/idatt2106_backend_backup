package org.ntnu.idatt2106.backend.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ntnu.idatt2106.backend.controller.SecurityConfigTestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SecurityConfigTestController.class)
@Import(SecurityConfig.class)
public class SecurityConfigTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("Should allow public access to swagger endpoint")
  void testSwaggerUiIsPublic() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/swagger-ui/index.html"))
            .andExpect(status().isOk())
            .andExpect(content().string("swagger OK"));
  }

  @Test
  @DisplayName("Should require authentication for protected endpoint")
  void testSecureEndpointRequiresAuth() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/secure"))
            .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should allow access to protected endpoint with authentication")
  @WithMockUser
  void testSecureEndpointWithAuth() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/secure"))
            .andExpect(status().isOk())
            .andExpect(content().string("secure OK"));
  }
}
