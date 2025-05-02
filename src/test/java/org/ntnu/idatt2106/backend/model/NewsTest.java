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
            "Test Case ID",
            "This is a test news article",
            12.34,
            56.78,
            "Test District",
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
    News news = new News("Test Title", "Test Case ID", "This is a test news article", 12.34, 56.78, "Test District", new Date());

    assertNotNull(news);
    assertEquals("Test Title", news.getTitle());
    assertEquals("Test Case ID", news.getCaseId());
    assertEquals("This is a test news article", news.getContent());
    assertEquals(12.34, news.getLatitude());
    assertEquals(56.78, news.getLongitude());
    assertEquals("Test District", news.getDistrict());
    assertNotNull(news.getDate());
  }

  @Test
  @DisplayName("Set and Get Title")
  void setAndGetTitle() {
    testNews.setTitle("New Title");
    assertEquals("New Title", testNews.getTitle());
  }

  @Test
  @DisplayName("Set and Get Content")
  void setAndGetContent() {
    testNews.setContent("New Content");
    assertEquals("New Content", testNews.getContent());
  }

  @Test
  @DisplayName("Set and Get Case ID")
  void setAndGetCaseId() {
    testNews.setCaseId("New Case ID");
    assertEquals("New Case ID", testNews.getCaseId());
  }

  @Test
  @DisplayName("Set and Get Latitude")
  void setAndGetLatitude() {
    testNews.setLatitude(90.0);
    assertEquals(90.0, testNews.getLatitude());
  }

  @Test
  @DisplayName("Set and Get Longitude")
  void setAndGetLongitude() {
    testNews.setLongitude(180.0);
    assertEquals(180.0, testNews.getLongitude());
  }

  @Test
  @DisplayName("Set and Get District")
  void setAndGetDistrict() {
    testNews.setDistrict("New District");
    assertEquals("New District", testNews.getDistrict());
  }

  @Test
  @DisplayName("Set and Get Date")
  void setAndGetDate() {
    Date newDate = new Date();
    testNews.setDate(newDate);
    assertEquals(newDate, testNews.getDate());
  }
}
