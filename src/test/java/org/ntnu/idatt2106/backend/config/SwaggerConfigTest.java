package org.ntnu.idatt2106.backend.config;

import static org.junit.jupiter.api.Assertions.*;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SwaggerConfig.class)
class SwaggerConfigTest {

  @Autowired
  private OpenAPI openAPI;

  @Test
  @DisplayName("OpenAPI bean should not be null")
  void testOpenAPIBeanCreated() {
    assertNotNull(openAPI);
  }

  @Test
  @DisplayName("OpenAPI should contain expected title, version and description")
  void testOpenAPIInfo() {
    Info info = openAPI.getInfo();
    assertNotNull(info);
    assertEquals("BackendAPI", info.getTitle());
    assertEquals("1.0.0", info.getVersion());
    assertEquals("Backend API for the application", info.getDescription());
  }

  @Test
  @DisplayName("OpenAPI should define bearerAuth security scheme")
  void testSecuritySchemeExists() {
    SecurityScheme securityScheme = openAPI.getComponents().getSecuritySchemes().get("bearerAuth");
    assertNotNull(securityScheme);
    assertEquals(SecurityScheme.Type.HTTP, securityScheme.getType());
    assertEquals("bearer", securityScheme.getScheme());
    assertEquals("JWT", securityScheme.getBearerFormat());
  }
}
