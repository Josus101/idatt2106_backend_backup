package org.ntnu.idatt2106.backend.model;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class NewsTest {

  News testNews;

  @BeforeEach
  void setUp() {
    testNews = new News(
            "Test Title",
            "This is a test news article",
            12.34,
            56.78,
            new Date());
  }

  @Test
  @DisplayName("Default constructor creates new instance")
  void defaultConstructorCreatedInstance() {
    News news = new News();
    assertNotNull(news);
  }

  @Test
  @DisplayName("All Args constructor sets all fields correctly")
  void allArgsConstructorSetsFieldsCorrectly() {
    News news = new News("Test Title", "This is a test news article", 12.34, 56.78, new Date());

    assertNotNull(news);
  }

}
