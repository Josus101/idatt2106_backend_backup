package org.ntnu.idatt2106.backend.dto.news;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NewsGetResponseTest {

  private NewsGetResponse newsGetResponse;

  @BeforeEach
  void setUp() {
    newsGetResponse = new NewsGetResponse(
            1,
            "25h7fg",
            "News title",
            "News content",
            60.39299,
            5.32415,
            "Oslo",
            "2025-04-30 12:59:03");
  }

  @Test
  @DisplayName("Test the constructor of NewsGetResponse")
  void testConstructor() {
    assertNotNull(newsGetResponse);
    assertEquals(1, newsGetResponse.getId());
    assertEquals("25h7fg", newsGetResponse.getCaseId());
    assertEquals("News title", newsGetResponse.getTitle());
    assertEquals("News content", newsGetResponse.getContent());
    assertEquals(60.39299, newsGetResponse.getLatitude());
    assertEquals(5.32415, newsGetResponse.getLongitude());
    assertEquals("Oslo", newsGetResponse.getDistrict());
    assertEquals("2025-04-30 12:59:03", newsGetResponse.getDate());
  }

  @Test
  @DisplayName("Test the toString method of NewsGetResponse")
  void testToString() {
    String expectedString = "NewsGetResponse(id=1, caseId=25h7fg, title=News title, content=News content, latitude=60.39299, longitude=5.32415, district=Oslo, date=2025-04-30 12:59:03)";
    assertEquals(expectedString, newsGetResponse.toString());
  }

  @Test
  @DisplayName("Test the getters of NewsGetResponse")
  void testGetters() {
    assertEquals(1, newsGetResponse.getId());
    assertEquals("25h7fg", newsGetResponse.getCaseId());
    assertEquals("News title", newsGetResponse.getTitle());
    assertEquals("News content", newsGetResponse.getContent());
    assertEquals(60.39299, newsGetResponse.getLatitude());
    assertEquals(5.32415, newsGetResponse.getLongitude());
    assertEquals("Oslo", newsGetResponse.getDistrict());
    assertEquals("2025-04-30 12:59:03", newsGetResponse.getDate());
  }

  @Test
  @DisplayName("Test the setters of NewsGetResponse")
  void testSetters() {
    newsGetResponse.setId(2);
    newsGetResponse.setCaseId("newCaseId");
    newsGetResponse.setTitle("New title");
    newsGetResponse.setContent("New content");
    newsGetResponse.setLatitude(60.12345);
    newsGetResponse.setLongitude(5.67890);
    newsGetResponse.setDistrict("Bergen");
    newsGetResponse.setDate("2025-05-01 12:00:00");

    assertEquals(2, newsGetResponse.getId());
    assertEquals("newCaseId", newsGetResponse.getCaseId());
    assertEquals("New title", newsGetResponse.getTitle());
    assertEquals("New content", newsGetResponse.getContent());
    assertEquals(60.12345, newsGetResponse.getLatitude());
    assertEquals(5.67890, newsGetResponse.getLongitude());
    assertEquals("Bergen", newsGetResponse.getDistrict());
    assertEquals("2025-05-01 12:00:00", newsGetResponse.getDate());
  }

}