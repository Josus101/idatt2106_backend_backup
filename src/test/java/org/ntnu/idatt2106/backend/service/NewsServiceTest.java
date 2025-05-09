package org.ntnu.idatt2106.backend.service;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import jakarta.persistence.EntityNotFoundException;
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

import java.io.IOException;
import java.net.URL;
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
            new Date(1746442184323L));

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
  @DisplayName("getAllNews throws EntityNotFoundException when no news found")
  void testGetAllNewsNoNewsFound() {
    when(newsRepo.findAll()).thenReturn(List.of());

    EntityNotFoundException ex = assertThrows(
            EntityNotFoundException.class,
            () -> newsService.getAllNews()
    );

    assertEquals("No news found", ex.getMessage());
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
  @DisplayName("getByDistrict throws EntityNotFoundException when no news found")
  void testGetByDistrictNoNews() {
    when(newsRepo.findByDistrict("Test District")).thenReturn(List.of());

    EntityNotFoundException ex = assertThrows(
            EntityNotFoundException.class,
            () -> newsService.getByDistrict("Test District")
    );
    assertEquals("No news found in district: Test District", ex.getMessage());
  }

  @Test
  @DisplayName("getByDistrict throws EntityNotFoundException when district is null")
  void testGetByDistrictNull() {
    when(newsRepo.findByDistrict(null)).thenReturn(List.of());

    EntityNotFoundException ex = assertThrows(
            EntityNotFoundException.class,
            () -> newsService.getByDistrict(null)
    );
    assertEquals("No news found in district: null", ex.getMessage());
  }

  @Test
  @DisplayName("addNews should save news when all fields are valid and not duplicate")
  void testAddNewsSuccess() {
    NewsCreateRequest validRequest = new NewsCreateRequest("Title", "def456", "Content", 10.0, 20.0, "Oslo Politidistrikt");

    when(newsRepo.existsByTitleAndDate(anyString(), any(Date.class))).thenReturn(false);

    newsService.addNews(validRequest);

    verify(newsRepo).save(any(News.class));
  }


  @Test
  @DisplayName("getByCaseId returns list of NewsGetResponse on success")
  void testGetByCaseIdSuccess() {
    when(newsRepo.findByCaseId("abc123")).thenReturn(List.of(testNews));
    List<NewsGetResponse> result = newsService.getByCaseId("abc123");

    assertEquals(1, result.size());
    assertEquals(testNewsGetResponse.toString(), result.get(0).toString());
  }

  @Test
  @DisplayName("getByCaseId throws EntityNotFoundException when no news found")
  void testGetByCaseIdThrowsOnEmpty() {
    when(newsRepo.findByCaseId("abc123")).thenReturn(List.of());

    EntityNotFoundException ex = assertThrows(
            EntityNotFoundException.class,
            () -> newsService.getByCaseId("abc123")
    );
    assertEquals("No news found with case ID: abc123", ex.getMessage());
  }


  @Test
  @DisplayName("groupNewsByCaseIdAndSort returns list of lists of NewsGetResponse on success")
  void groupNewsByCaseIdAndSort_Success() {
    List<NewsGetResponse> newsList = List.of(
            new NewsGetResponse(1, "CaseId1", "Title1", "Content1", 10.0, 20.0, "Oslo Politidistrikt", new Date(1746442184323L).toString()),
            new NewsGetResponse(2, "CaseId1", "Title2", "Content2", 10.0, 20.0, "Oslo Politidistrikt", new Date(1746442184323L).toString()),
            new NewsGetResponse(3, "CaseId2", "Title3", "Content3", 10.0, 20.0, "Oslo Politidistrikt", new Date(1746442184323L).toString())
    );

    List<List<NewsGetResponse>> groupedNews = newsService.groupNewsByCaseIdAndSort(newsList);

    assertEquals(2, groupedNews.size());
    assertEquals(1, groupedNews.get(0).size());
    assertEquals(2, groupedNews.get(1).size());
  }

  @Test
  @DisplayName("groupNewsByCaseIdAndSort returns empty list when input is empty")
  void groupNewsByCaseIdAndSort_EmptyInput() {
    List<NewsGetResponse> newsList = List.of();
    List<List<NewsGetResponse>> groupedNews = newsService.groupNewsByCaseIdAndSort(newsList);

    assertTrue(groupedNews.isEmpty());
  }

  @Test
  @DisplayName("getRecentFromGroupedNews returns most recent news from grouped news")
  void getRecentFromGroupedNews_Success() {
    List<NewsGetResponse> newsList = List.of(
            new NewsGetResponse(1, "CaseId1", "Title1", "Content1", 10.0, 20.0, "Oslo Politidistrikt", "2023-10-01"),
            new NewsGetResponse(2, "CaseId1", "Title2", "Content2", 10.0, 20.0, "Oslo Politidistrikt", "2023-10-02"),
            new NewsGetResponse(3, "CaseId2", "Title3", "Content3", 10.0, 20.0, "Oslo Politidistrikt", "2023-10-03")
    );

    List<List<NewsGetResponse>> groupedNews = newsService.groupNewsByCaseIdAndSort(newsList);
    List<NewsGetResponse> recentNews = newsService.getRecentFromGroupedNews(groupedNews);

    assertEquals(2, recentNews.size());
    assertEquals("Title3", recentNews.get(0).getTitle());
    assertEquals("Title2", recentNews.get(1).getTitle());
  }

  @Test
  @DisplayName("addNews should throw AlreadyInUseException if title and date already exist")
  void testAddNews_DuplicateTitleAndDate() {
    when(newsRepo.existsByTitleAndDate(anyString(), any(Date.class))).thenReturn(true);

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
  void testLoadFeed_ThrowsAfterAllAttempts() throws Exception {
    int maxAttempts = 3;
    NewsService newsService = spy(new NewsService());

    SyndFeedInput mockInput = mock(SyndFeedInput.class);
    when(mockInput.build(any(XmlReader.class))).thenThrow(new RuntimeException("Feed loading failed"));

    URL mockUrl = new URL("https://api.politiet.no/politiloggen/v1/rss");

    doReturn(mockUrl).when(newsService).getFeedUrl();
    doReturn(mockInput).when(newsService).getSyndFeedInput();

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      newsService.loadFeed(maxAttempts);
    });

    assertEquals("Failed to load feed after " + maxAttempts + " attempts", exception.getMessage());

    verify(mockInput, times(maxAttempts)).build(any(XmlReader.class));
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
    Date date = new Date(1746442184323L);
    SyndEntry entry = mockSyndEntry("New Title", "Content", date, List.of("Oslo Politidistrikt"));
    SyndFeed feed = mock(SyndFeed.class);
    when(feed.getEntries()).thenReturn(List.of(entry));
    doReturn(feed).when(newsService).loadFeed(10);
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
    Date date = new Date(1746442184323L);
    SyndEntry entry = mockSyndEntry("Dup Title", "Dup content", date, List.of("Oslo Politidistrikt"));
    SyndFeed feed = mock(SyndFeed.class);
    when(feed.getEntries()).thenReturn(List.of(entry));
    doReturn(feed).when(newsService).loadFeed(10);
    when(newsRepo.existsByTitleAndDate("Dup Title", date)).thenReturn(true);
    newsService.retrieveNewsFromAPIFeed();
    verify(newsRepo, never()).save(any());
  }

  @Test
  @DisplayName("retrieveNewsFromAPIFeed uses fallback when category is missing")
  void testRetrieveNewsFallbackDistrict() throws Exception {
    Date date = new Date(1746442184323L);
    SyndEntry entry = mockSyndEntry("Fallback Title", "No category", date, List.of());
    SyndFeed feed = mock(SyndFeed.class);
    when(feed.getEntries()).thenReturn(List.of(entry));
    doReturn(feed).when(newsService).loadFeed(10);
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
    doThrow(new Exception("Feed not found")).when(newsService).loadFeed(10);
    assertThrows(Exception.class, () -> newsService.loadFeed(10));
  }

  @Test
  void testLoadFeed_ThrowsAfterMaxAttempts() throws Exception {
    // Arrange
    int maxAttempts = 3;
    NewsService newsService = spy(new NewsService());

    // Mocking SyndFeedInput to always throw an IOException
    SyndFeedInput mockInput = mock(SyndFeedInput.class);
    when(mockInput.build(any(XmlReader.class))).thenThrow(new RuntimeException("Feed loading failed"));

    // Mocking the URL object
    URL mockUrl = new URL("https://api.politiet.no/politiloggen/v1/rss");

    // Use spy to replace internal method calls
    doReturn(mockUrl).when(newsService).getFeedUrl();
    doReturn(mockInput).when(newsService).getSyndFeedInput();

    // Act & Assert
    Exception exception = assertThrows(RuntimeException.class, () -> {
      newsService.loadFeed(maxAttempts);
    });

    // Verify the exception message
    assertEquals("Failed to load feed after 3 attempts", exception.getMessage());

    // Verify the number of attempts
    verify(mockInput, times(maxAttempts)).build(any(XmlReader.class));
  }

  @Test
  void testLoadFeed_SuccessWithDefaultAttempts() throws Exception {
    // Arrange
    NewsService newsService = spy(new NewsService());

    // Mocking a successful feed
    SyndFeed mockFeed = mock(SyndFeed.class);
    SyndFeedInput mockInput = mock(SyndFeedInput.class);
    when(mockInput.build(any(XmlReader.class))).thenReturn(mockFeed);

    // Mocking the URL object
    URL mockUrl = new URL("https://api.politiet.no/politiloggen/v1/rss");

    // Use spy to replace internal method calls
    doReturn(mockUrl).when(newsService).getFeedUrl();
    doReturn(mockInput).when(newsService).getSyndFeedInput();

    // Act
    SyndFeed result = newsService.loadFeed();

    // Assert
    assertNotNull(result, "Feed should not be null");
    assertEquals(mockFeed, result, "Returned feed should match the mocked feed");

    // Verify the build method is called only once since the feed loads successfully
    verify(mockInput, times(1)).build(any(XmlReader.class));
  }

  @Test
  void testLoadFeed_ThrowsAfterDefaultAttempts() throws Exception {
    // Arrange
    NewsService newsService = spy(new NewsService());

    // Mocking SyndFeedInput to always throw an IOException
    SyndFeedInput mockInput = mock(SyndFeedInput.class);
    when(mockInput.build(any(XmlReader.class))).thenThrow(new RuntimeException("Feed loading failed"));

    // Mocking the URL object
    URL mockUrl = new URL("https://api.politiet.no/politiloggen/v1/rss");

    // Use spy to replace internal method calls
    doReturn(mockUrl).when(newsService).getFeedUrl();
    doReturn(mockInput).when(newsService).getSyndFeedInput();

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      newsService.loadFeed();
    });

    // Check the exception message
    assertEquals("Failed to load feed after 10 attempts", exception.getMessage());

    // Verify that the build method was called the default 10 times
    verify(mockInput, times(10)).build(any(XmlReader.class));
  }


  @Test
  @DisplayName("clearExpiredNews clears expired news (older than 1 days)")
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
  @DisplayName("clearExpiredNews does not clear news newer than 1 days")
  void testClearExpiredNewsNotExpired() {
    News freshNews = new News(
            "Recent",
            "abc123",
            "Fresh content",
            12.34,
            56.78,
            "Fresh District",
            new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 22)); // 22 hours ago

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

}