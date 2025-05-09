package org.ntnu.idatt2106.backend.service;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import jakarta.persistence.EntityNotFoundException;
import org.ntnu.idatt2106.backend.dto.news.NewsCreateRequest;
import org.ntnu.idatt2106.backend.dto.news.NewsGetResponse;
import org.ntnu.idatt2106.backend.exceptions.AlreadyInUseException;
import org.ntnu.idatt2106.backend.model.News;
import org.ntnu.idatt2106.backend.repo.NewsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for the News model
 * @Author Jonas Reiher
 * @since 0.2
 * @version 0.3
 */
@Service
public class NewsService {

  @Autowired
  private NewsRepo newsRepo;

  /**
   * On application startup, retrieve news from the Politiloggen API
   */
  @EventListener(ApplicationReadyEvent.class)
  public void initOnStartup() {
    retrieveNewsFromAPIFeed();
    clearExpiredNews();
  }

  /**
   * Method to get all news from the database
   * @return List of NewsGetResponse
   * @throws EntityNotFoundException if no news is found
   */
  public List<NewsGetResponse> getAllNews() throws EntityNotFoundException {
    List<NewsGetResponse> allNews = newsRepo.findAll().stream()
        .map(news -> new NewsGetResponse(
            news.getId(), news.getCaseId(),
            news.getTitle(),
            news.getContent(),
            news.getLatitude(),
            news.getLongitude(),
            news.getDistrict(),
            news.getDate().toString()
    )).toList();

    if (allNews.isEmpty()) {
      throw new EntityNotFoundException("No news found");
    }

    return allNews;
  }

  /**
   * Method to get news by district
   * @param district the district to get news from
   * @return List of NewsGetResponse
   * @throws EntityNotFoundException if no news is found
   */
  public List<NewsGetResponse> getByDistrict(String district) {
    List<NewsGetResponse> newsByDistrict =  newsRepo.findByDistrict(district).stream()
        .map(news -> new NewsGetResponse(
            news.getId(),
            news.getCaseId(),
            news.getTitle(),
            news.getContent(),
            news.getLatitude(),
            news.getLongitude(),
            news.getDistrict(),
            news.getDate().toString()
        )).toList();

    if (newsByDistrict.isEmpty()) {
      throw new EntityNotFoundException("No news found in district: " + district);
    }
    return newsByDistrict;
  }

  /**
   * Method to get news by case ID
   * @param caseId the case ID to get news from
   * @return List of NewsGetResponse
   * @throws EntityNotFoundException if no news is found
   */
  public List<NewsGetResponse> getByCaseId(String caseId) {
    List<NewsGetResponse> newsByCaseId =  newsRepo.findByCaseId(caseId).stream()
        .map(news -> new NewsGetResponse(
            news.getId(),
            news.getCaseId(),
            news.getTitle(),
            news.getContent(),
            news.getLatitude(),
            news.getLongitude(),
            news.getDistrict(),
            news.getDate().toString()
        )).toList();

    if (newsByCaseId.isEmpty()) {
      throw new EntityNotFoundException("No news found with case ID: " + caseId);
    }
    return newsByCaseId;
  }

  /**
   * Method to group news by case ID and sort by date
   * @param news the list of news to group and sort
   * @return List of grouped and sorted news
   */
  public List<List<NewsGetResponse>> groupNewsByCaseIdAndSort(List<NewsGetResponse> news) {
    return news.stream()
            .collect(Collectors.groupingBy(NewsGetResponse::getCaseId))
            .values().stream()
            .map(list -> list.stream()
                    .sorted(Comparator.comparing(NewsGetResponse::getDate).reversed())
                    .collect(Collectors.toList()))
            .toList().stream()
                .sorted(Comparator.comparing(
                        (List<NewsGetResponse> l) -> l.getFirst().getDate()).reversed()
                ).collect(Collectors.toList());
  }

  /**
   * Method to get the most recent news from grouped news
   * @param groupedNews the grouped news to get the most recent from
   * @return List of most recent news
   */
  public List<NewsGetResponse> getRecentFromGroupedNews(List<List<NewsGetResponse>> groupedNews) {
    return groupedNews.stream()
            .map(List::getFirst)
            .collect(Collectors.toList());
  }

  /**
   * Method to add news to the database
   * @param newsCreateRequest the news to add
   * @throws AlreadyInUseException if the news already exists
   * @throws IllegalArgumentException if the news is invalid
   */
  public void addNews(NewsCreateRequest newsCreateRequest) {
    Date now = new Date();
    if (newsRepo.existsByTitleAndDate(newsCreateRequest.getTitle(), now)) {
      throw new AlreadyInUseException("News with the same title and date already exists");
    }

    if (newsCreateRequest.getTitle().isEmpty() || newsCreateRequest.getContent().isEmpty() || newsCreateRequest.getDistrict().isEmpty()) {
      throw new IllegalArgumentException("Title, content and district cannot be empty");
    }

    News news = new News(
            newsCreateRequest.getTitle(),
            newsCreateRequest.getCaseId(),
            newsCreateRequest.getContent(),
            newsCreateRequest.getLatitude(),
            newsCreateRequest.getLongitude(),
            newsCreateRequest.getDistrict(),
            now);
    System.out.println("Adding news: " + news.getTitle() + " " + news.getContent() + " " + news.getDistrict() + " " + news.getDate() + " " + news.getCaseId() + " " + news.getLatitude() + " " + news.getLongitude());
    newsRepo.save(news);
  }

  /**
   * Method to get the SyndFeedInput object
   * @return SyndFeedInput
   */
  protected SyndFeedInput getSyndFeedInput() {
    return new SyndFeedInput();
  }

  /**
   * Method to get the feed URL from the Politiloggen API
   * @return URL of the feed
   * @throws Exception if the URL cannot be created
   */
  protected URL getFeedUrl() throws Exception {
    return new URL("https://api.politiet.no/politiloggen/v1/rss");
  }

  /**
   * Method to load the feed from the Politiloggen API
   * @param maxAttempts the maximum number of attempts to load the feed
   * @return SyndFeed
   * @throws Exception if the feed cannot be loaded
   */
  protected SyndFeed loadFeed(int maxAttempts) throws Exception {
    int attemptCount = 0;

    while (attemptCount < maxAttempts) {
      try {
        URL feedUrl = getFeedUrl();
        SyndFeedInput input = getSyndFeedInput();
        return input.build(new XmlReader(feedUrl));
      } catch (Exception e) {
        attemptCount++;
        if (attemptCount >= maxAttempts) {
          break;
        }
        Thread.sleep(1000);
      }
    }
    throw new RuntimeException("Failed to load feed after " + maxAttempts + " attempts");
  }


  /**
   * Method to load the feed from the Politiloggen API
   * @return SyndFeed
   * @throws Exception if the feed cannot be loaded
   */
  protected SyndFeed loadFeed() throws Exception {
    return loadFeed(10);
  }

  /**
   * Scheduled method to retrieve news from the Politiloggen API
   * This method is called every 5 minutes
   * @throws RuntimeException if the feed cannot be loaded
   */
  @Scheduled(fixedRate = 300_000) // 5 minutes
  public void retrieveNewsFromAPIFeed() {
    try {
      SyndFeed feed = loadFeed(10);

      System.out.println("Retrieving news from API...");
      System.out.println("Feed items length: " + feed.getEntries().size());

      for (SyndEntry entry : feed.getEntries()) {
        String rawTitle = entry.getTitle();
        String content = entry.getDescription().getValue();
        Date publishedDate = entry.getPublishedDate();

        List<SyndCategory> categories = entry.getCategories();
        String district = categories.isEmpty() ? "Ukjent distrikt" : categories.get(0).getName();

        double lat = 0.0;
        double lon = 0.0;

        String caseId = "";
        String title = rawTitle;
        if (rawTitle != null && rawTitle.contains("(ID:")) {
          int start = rawTitle.indexOf("(ID:");
          int end = rawTitle.indexOf(")", start);
          if (start != -1 && end != -1) {
            caseId = rawTitle.substring(start + 5, end).trim();
            title = rawTitle.substring(0, start).trim();
          }
        }

        News news = new News();
        news.setTitle(title);
        news.setContent(content);
        news.setLatitude(lat);
        news.setLongitude(lon);
        news.setDistrict(district);
        news.setDate(publishedDate);
        news.setCaseId(caseId);

        if (!newsRepo.existsByTitleAndDate(title, publishedDate)) {
          newsRepo.save(news);
        }
      }

    } catch (Exception e) {
      System.err.println("Error retrieving news from API: " + e.getMessage());
    }
  }

  /**
   * Scheduled method to clear expired news
   * This method is called every 24 hours
   */
  @Scheduled(fixedRate = 3_600_000) // 1 hour
  public void clearExpiredNews() {
    System.out.println("Clearing expired news...");
    long timeAlive = 86_400_000; // 1 day(s) in millis
    Date now = new Date();
    List<News> expiredNews = newsRepo.findAll().stream()
            .filter(news -> now.getTime() - news.getDate().getTime() > timeAlive)
            .toList();
    if (!expiredNews.isEmpty()) {
      newsRepo.deleteAll(expiredNews);
    }
  }

  /**
   * Method to delete news by ID
   * @param id the ID of the news to delete
   */
  public void deleteNews(String id) {
    List<News> news = newsRepo.findByCaseId(id);
    if (news.isEmpty()) {
      throw new EntityNotFoundException("News with ID: " + id + " not found");
    }

    newsRepo.deleteAll(news);
  }
}
