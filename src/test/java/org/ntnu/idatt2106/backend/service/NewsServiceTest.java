package org.ntnu.idatt2106.backend.service;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ntnu.idatt2106.backend.dto.news.NewsCreateRequest;
import org.ntnu.idatt2106.backend.dto.news.NewsGetResponse;
import org.ntnu.idatt2106.backend.exceptions.AlreadyInUseException;
import org.ntnu.idatt2106.backend.model.News;
import org.ntnu.idatt2106.backend.repo.NewsRepo;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for the NewsService
 * @Author Jonas Reiher
 * @since 0.2
 * @version 0.2
 */
@ExtendWith(MockitoExtension.class)
public class NewsServiceTest {

  @Spy
  @InjectMocks
  private NewsService newsService;

  @Mock
  private NewsRepo newsRepo;

  News testNews;
  NewsGetResponse testNewsGetResponse;

  NewsCreateRequest validRequest;

  @BeforeEach
  void setUp() {
    testNews = new News(
            "Test Title",
            "abc123",
            "This is a test news article",
            12.34,
            56.78,
            "Test District",
            new Date());

    testNewsGetResponse = new NewsGetResponse(
            testNews.getId(),
            testNews.getCaseId(),
            testNews.getTitle(),
            testNews.getContent(),
            testNews.getLatitude(),
            testNews.getLongitude(),
            testNews.getDistrict(),
            testNews.getDate().toString());

    validRequest = new NewsCreateRequest("Title", "def456",  "Content", 10.0, 20.0, "Oslo Politidistrikt");
  }

  @Test
  @DisplayName("getAllNews returns list of NewsGetResponse on success")
  void testGetAllNewsSuccess() {
    when(newsRepo.findAll()).thenReturn(List.of(testNews));
    List<NewsGetResponse> result = newsService.getAllNews();
    assertEquals(1, result.size());
    assertEquals(testNewsGetResponse.toString(), result.get(0).toString());
  }

  @Test
  @DisplayName("getAllNews returns empty list when no news found")
  void testGetAllNewsEmpty() {
    when(newsRepo.findAll()).thenReturn(List.of());
    List<NewsGetResponse> result = newsService.getAllNews();
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("getByDistrict returns list of NewsGetResponse on success")
  void testGetByDistrictSuccess() {
    when(newsRepo.findByDistrict("Test District")).thenReturn(List.of(testNews));
    List<NewsGetResponse> result = newsService.getByDistrict("Test District");
    assertEquals(1, result.size());
    assertEquals(testNewsGetResponse.toString(), result.get(0).toString());
  }

  @Test
  @DisplayName("getByDistrict returns empty list when no news found")
  void testGetByDistrictEmpty() {
    when(newsRepo.findByDistrict("Test District")).thenReturn(List.of());
    List<NewsGetResponse> result = newsService.getByDistrict("Test District");
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("getByDistrict returns empty list when district is null")
  void testGetByDistrictNull() {
    when(newsRepo.findByDistrict(null)).thenReturn(List.of());
    List<NewsGetResponse> result = newsService.getByDistrict(null);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("addNews should save news when all fields are valid and not duplicate")
  void testAddNewsSuccess() {
    when(newsRepo.existsByTitleAndDate(eq("Title"), any(Date.class))).thenReturn(false);

    newsService.addNews(validRequest);

    verify(newsRepo).save(any(News.class));
  }

  @Test
  @DisplayName("addNews should throw AlreadyInUseException if title and date already exist")
  void testAddNews_DuplicateTitleAndDate() {
    when(newsRepo.existsByTitleAndDate(eq("Title"), any(Date.class))).thenReturn(true);

    AlreadyInUseException ex = assertThrows(
            AlreadyInUseException.class,
            () -> newsService.addNews(validRequest)
    );
    assertEquals("News with the same title and date already exists", ex.getMessage());

    verify(newsRepo, never()).save(any());
  }

  @Test
  @DisplayName("addNews should throw IllegalArgumentException if title is empty")
  void testAddNews_EmptyTitle() {
    validRequest.setTitle("");

    IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> newsService.addNews(validRequest)
    );
    assertEquals("Title, content and district cannot be empty", ex.getMessage());

    verify(newsRepo, never()).save(any());
  }

  @Test
  @DisplayName("addNews should throw IllegalArgumentException if content is empty")
  void testAddNews_EmptyContent() {
    validRequest.setContent("");

    IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> newsService.addNews(validRequest)
    );
    assertEquals("Title, content and district cannot be empty", ex.getMessage());

    verify(newsRepo, never()).save(any());
  }

  @Test
  @DisplayName("addNews should throw IllegalArgumentException if district is empty")
  void testAddNews_EmptyDistrict() {
    validRequest.setDistrict("");

    IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> newsService.addNews(validRequest)
    );
    assertEquals("Title, content and district cannot be empty", ex.getMessage());

    verify(newsRepo, never()).save(any());
  }

  @Test
  @DisplayName("retrieveNewsFromAPIFeed saves new entries when not already in DB")
  void testRetrieveNewsAddsNewEntry() throws Exception {
    Date date = new Date();
    SyndEntry entry = mockSyndEntry("New Title", "Content", date, List.of("Oslo Politidistrikt"));
    SyndFeed feed = mock(SyndFeed.class);
    when(feed.getEntries()).thenReturn(List.of(entry));
    doReturn(feed).when(newsService).loadFeed();
    when(newsRepo.existsByTitleAndDate("New Title", date)).thenReturn(false);
    newsService.retrieveNewsFromAPIFeed();
    verify(newsRepo).save(argThat(news ->
            news.getTitle().equals("New Title") &&
                    news.getDistrict().equals("Oslo Politidistrikt")
    ));
  }

  @Test
  @DisplayName("retrieveNewsFromAPIFeed does not save duplicate news")
  void testRetrieveNewsSkipsDuplicate() throws Exception {
    Date date = new Date();
    SyndEntry entry = mockSyndEntry("Dup Title", "Dup content", date, List.of("Oslo Politidistrikt"));
    SyndFeed feed = mock(SyndFeed.class);
    when(feed.getEntries()).thenReturn(List.of(entry));
    doReturn(feed).when(newsService).loadFeed();
    when(newsRepo.existsByTitleAndDate("Dup Title", date)).thenReturn(true);
    newsService.retrieveNewsFromAPIFeed();
    verify(newsRepo, never()).save(any());
  }

  @Test
  @DisplayName("retrieveNewsFromAPIFeed uses fallback when category is missing")
  void testRetrieveNewsFallbackDistrict() throws Exception {
    Date date = new Date();
    SyndEntry entry = mockSyndEntry("Fallback Title", "No category", date, List.of());
    SyndFeed feed = mock(SyndFeed.class);
    when(feed.getEntries()).thenReturn(List.of(entry));
    doReturn(feed).when(newsService).loadFeed();
    when(newsRepo.existsByTitleAndDate("Fallback Title", date)).thenReturn(false);
    newsService.retrieveNewsFromAPIFeed();
    verify(newsRepo).save(argThat(news -> news.getDistrict().equals("Ukjent distrikt")));
  }

  private SyndEntry mockSyndEntry(String title, String content, Date date, List<String> categories) {
    SyndEntry entry = mock(SyndEntry.class);
    when(entry.getTitle()).thenReturn(title);
    when(entry.getPublishedDate()).thenReturn(date);
    SyndContent description = mock(SyndContent.class);
    when(description.getValue()).thenReturn(content);
    when(entry.getDescription()).thenReturn(description);
    List<SyndCategory> categoryMocks = categories.stream().map(name -> {
      SyndCategory cat = mock(SyndCategory.class);
      when(cat.getName()).thenReturn(name);
      return cat;
    }).toList();
    when(entry.getCategories()).thenReturn(categoryMocks);
    return entry;
  }

  @Test
  @DisplayName("loadFeed throws exception when feed cannot be loaded")
  void testLoadFeedThrowsException() throws Exception {
    doThrow(new Exception("Feed not found")).when(newsService).loadFeed();
    assertThrows(Exception.class, () -> newsService.loadFeed());
  }

  @Test
  @DisplayName("clearExpiredNews clears expired news (older than 2 days)")
  void testClearExpiredNews() {
    News expiredNews = new News(
            "Expired",
            "abc123",
            "Old content",
            12.34,
            56.78,
            "Expired District",
            new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 3)); // 3 days ago

    when(newsRepo.findAll()).thenReturn(List.of(expiredNews));
    newsService.clearExpiredNews();
    verify(newsRepo).deleteAll(List.of(expiredNews));
  }

  @Test
  @DisplayName("clearExpiredNews does not clear news newer than 2 days")
  void testClearExpiredNewsNotExpired() {
    News freshNews = new News(
            "Recent",
            "abc123",
            "Fresh content",
            12.34,
            56.78,
            "Fresh District",
            new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24)); // 1 day ago

    when(newsRepo.findAll()).thenReturn(List.of(freshNews));
    newsService.clearExpiredNews();

    // Explicitly verify that only findAll was called, nothing else
    verify(newsRepo).findAll();
    verifyNoMoreInteractions(newsRepo); // Ensures deleteAll wasn't called at all
  }



  @Test
  @DisplayName("initOnStartup calls retrieveNewsFromAPIFeed")
  void testInitOnStartupCallsRetrieveNews() {
    doNothing().when(newsService).retrieveNewsFromAPIFeed(); // use existing spy
    newsService.initOnStartup();
    verify(newsService).retrieveNewsFromAPIFeed();
  }


  @Test
  @DisplayName("retrieveNewsFromAPIFeed throws RuntimeException when feed fails")
  void testRetrieveNewsThrowsRuntimeException() throws Exception {
    doThrow(new Exception("Feed not found")).when(newsService).loadFeed();
    RuntimeException ex = assertThrows(RuntimeException.class, () -> newsService.retrieveNewsFromAPIFeed());
    assertTrue(ex.getMessage().contains("Failed to retrieve news from API"));
  }


}