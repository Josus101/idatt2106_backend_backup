package org.ntnu.idatt2106.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ntnu.idatt2106.backend.dto.news.NewsGetResponse;
import org.ntnu.idatt2106.backend.service.NewsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NewsControllerTest {

  // success test for getNews
  // Not found test for getNews

  // success test for retrieveNews
  // Internal server error test for retrieveNews

  // success test for getByDistrict
  // Not found test for getByDistrict
  // Internal server error test for getByDistrict

  @InjectMocks
  private NewsController newsController;

  @Mock
  private NewsService newsService;

  private NewsGetResponse newsResponse;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    newsResponse = new NewsGetResponse("Title", "Content", 10.0, 20.0, "Oslo Politidistrikt", new Date().toString());
  }

  @Test
  @DisplayName("getNews returns 200 - OK and list of news")
  void testGetNewsSuccess() {
    when(newsService.getAllNews()).thenReturn(List.of(newsResponse));
    ResponseEntity<?> response = newsController.getNews();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertInstanceOf(List.class, response.getBody());
  }

  @Test
  @DisplayName("getNews returns 404 - Not Found on exception")
  void testGetNewsNotFound() {
    when(newsService.getAllNews()).thenThrow(new RuntimeException("fail"));
    ResponseEntity<?> response = newsController.getNews();

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Error: No news found", response.getBody());
  }

  @Test
  @DisplayName("retrieveNews returns 200 - OK on success")
  void testRetrieveNewsSuccess() {
    doNothing().when(newsService).retrieveNewsFromAPIFeed();
    ResponseEntity<?> response = newsController.retrieveNews();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("News retrieved successfully", response.getBody());
  }

  @Test
  @DisplayName("retrieveNews returns 500 - Internal Server Error on error")
  void testRetrieveNewsError() {
    doThrow(new RuntimeException("fail")).when(newsService).retrieveNewsFromAPIFeed();
    ResponseEntity<?> response = newsController.retrieveNews();

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Error: Error retrieving news", response.getBody());
  }

  @Test
  @DisplayName("getByDistrict returns 200 - OK and news list")
  void testGetByDistrictSuccess() {
    when(newsService.getByDistrict("Oslo Politidistrikt")).thenReturn(List.of(newsResponse));
    ResponseEntity<?> response = newsController.getByDistrict("Oslo");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertInstanceOf(List.class, response.getBody());
  }

  @Test
  @DisplayName("getByDistrict returns 404 - Not Found for empty list")
  void testGetByDistrictNotFound() {
    when(newsService.getByDistrict("Oslo Politidistrikt")).thenReturn(List.of());
    ResponseEntity<?> response = newsController.getByDistrict("Oslo");

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertTrue(response.getBody().toString().contains("Error: No news found"));
  }

  @Test
  @DisplayName("getByDistrict returns 500 on exception")
  void testGetByDistrictError() {
    when(newsService.getByDistrict("Oslo Politidistrikt")).thenThrow(new RuntimeException("boom"));
    ResponseEntity<?> response = newsController.getByDistrict("Oslo");

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Error: Error retrieving news", response.getBody());
  }



}