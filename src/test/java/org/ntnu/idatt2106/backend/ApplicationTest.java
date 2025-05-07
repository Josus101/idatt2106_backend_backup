package org.ntnu.idatt2106.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ntnu.idatt2106.backend.service.NewsService;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
class ApplicationTest {
  @Mock
  private NewsService newsService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Application context loads successfully")
  void contextLoads() {
    // This will fail if Spring Boot can't start the context.
  }

  @Test
  @DisplayName("Application main method runs without exceptions")
  void testMainMethod() {
    doNothing().when(newsService).retrieveNewsFromAPIFeed();
    when(newsService.getAllNews()).thenReturn(Collections.emptyList());

    Application.main(new String[] {});

  }
}
