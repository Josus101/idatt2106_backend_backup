package org.ntnu.idatt2106.backend.dto.news;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NewsCreateRequestTest {

  private NewsCreateRequest newsCreateRequest;

  @BeforeEach
  void setUp() {
    newsCreateRequest = new NewsCreateRequest(
            "25h7fg",
            "News title",
            "News content",
            60.39299,
            5.32415,
            "Oslo"
    );
  }

  @Test
  @DisplayName("Test the constructor of NewsGetResponse")
  void testConstructor() {
    assertNotNull(newsCreateRequest);

    assertEquals("25h7fg", newsCreateRequest.getCaseId());
    assertEquals("News title", newsCreateRequest.getTitle());
    assertEquals("News content", newsCreateRequest.getContent());
    assertEquals(60.39299, newsCreateRequest.getLatitude());
    assertEquals(5.32415, newsCreateRequest.getLongitude());
    assertEquals("Oslo", newsCreateRequest.getDistrict());

  }

  @Test
  @DisplayName("Test the toString method of NewsGetResponse")
  void testToString() {
    String expectedString = "NewsCreateRequest(caseId=25h7fg, title=News title, content=News content, latitude=60.39299, longitude=5.32415, district=Oslo)";
    assertEquals(expectedString, newsCreateRequest.toString());
  }

  @Test
  @DisplayName("Test the getters of NewsGetResponse")
  void testGetters() {
    assertEquals("25h7fg", newsCreateRequest.getCaseId());
    assertEquals("News title", newsCreateRequest.getTitle());
    assertEquals("News content", newsCreateRequest.getContent());
    assertEquals(60.39299, newsCreateRequest.getLatitude());
    assertEquals(5.32415, newsCreateRequest.getLongitude());
    assertEquals("Oslo", newsCreateRequest.getDistrict());
  }

  @Test
  @DisplayName("Test the setters of NewsGetResponse")
  void testSetters() {
    newsCreateRequest.setCaseId("newCaseId");
    newsCreateRequest.setTitle("New title");
    newsCreateRequest.setContent("New content");
    newsCreateRequest.setLatitude(60.12345);
    newsCreateRequest.setLongitude(5.67890);
    newsCreateRequest.setDistrict("Bergen");

    assertEquals("newCaseId", newsCreateRequest.getCaseId());
    assertEquals("New title", newsCreateRequest.getTitle());
    assertEquals("New content", newsCreateRequest.getContent());
    assertEquals(60.12345, newsCreateRequest.getLatitude());
    assertEquals(5.67890, newsCreateRequest.getLongitude());
    assertEquals("Bergen", newsCreateRequest.getDistrict());
  }

}