package org.ntnu.idatt2106.backend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTest {

  @Test
  @DisplayName("Application context loads successfully")
  void contextLoads() {
    // If the application context cannot be loaded, this test will fail.
  }

  @Test
  @DisplayName("Application main method runs without exceptions")
  void testMainMethod() {
    Application.main(new String[] {});
  }

}
