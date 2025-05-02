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
  }

  /**
   * Method to get all news from the database
   * @return List of NewsGetResponse
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
            .collect(Collectors.toList());
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
   */
  public void addNews(NewsCreateRequest newsCreateRequest) {
    if (newsRepo.existsByTitleAndDate(newsCreateRequest.getTitle(), new Date())) {
      throw new AlreadyInUseException("News with the same title and date already exists");
    }

    if (newsCreateRequest.getTitle().isEmpty() || newsCreateRequest.getContent().isEmpty() || newsCreateRequest.getDistrict().isEmpty()) {
      throw new IllegalArgumentException("Title, content and district cannot be empty");
    }

    News news = new News(
            newsCreateRequest.getTitle(),
            newsCreateRequest.getContent(),
            newsCreateRequest.getCaseId(),
            newsCreateRequest.getLatitude(),
            newsCreateRequest.getLongitude(),
            newsCreateRequest.getDistrict(),
            new Date());

    newsRepo.save(news);
  }

  /**
   * Method to load the feed from the Politiloggen API
   * @return SyndFeed
   * @throws Exception if the feed cannot be loaded
   */
  protected SyndFeed loadFeed() throws Exception {
    URL feedUrl = new URL("https://api.politiet.no/politiloggen/v1/rss");
    SyndFeedInput input = new SyndFeedInput();
    return input.build(new XmlReader(feedUrl));
  }

  /**
   * Scheduled method to retrieve news from the Politiloggen API
   * This method is called every 5 minutes
   */
  @Scheduled(fixedRate = 300_000) // sec_millis -> 5 minutes
  public void retrieveNewsFromAPIFeed() {
    try {
      SyndFeed feed = loadFeed();

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

        // Extract case ID using regex and remove from title
        String caseId = "";
        String title = rawTitle;
        if (rawTitle != null && rawTitle.contains("(ID:")) {
          int start = rawTitle.indexOf("(ID:");
          int end = rawTitle.indexOf(")", start);
          if (start != -1 && end != -1) {
            caseId = rawTitle.substring(start + 5, end).trim(); // Get content inside (ID: ...)
            title = rawTitle.substring(0, start).trim(); // Remove (ID: ...) from title
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
      throw new RuntimeException("Failed to retrieve news from API", e);
    }
  }

  /**
   * Scheduled method to clear expired news
   * This method is called every 24 hours
   */
  @Scheduled(fixedRate = 86_400_000) // sec_millis -> 24 hours
  public void clearExpiredNews() {
    long timeAlive = 86_400_000 * 2; // 2 days in millis
    Date now = new Date();
    List<News> expiredNews = newsRepo.findAll().stream()
            .filter(news -> now.getTime() - news.getDate().getTime() > timeAlive)
            .toList();
    if (!expiredNews.isEmpty()) {
      newsRepo.deleteAll(expiredNews);
    }
  }

}
