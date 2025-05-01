package org.ntnu.idatt2106.backend.service;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.ntnu.idatt2106.backend.dto.news.NewsGetResponse;
import org.ntnu.idatt2106.backend.model.News;
import org.ntnu.idatt2106.backend.repo.NewsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * Service for the News model
 * @Author Jonas Reiher
 * @since 0.1
 * @version 0.1
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
  public List<NewsGetResponse> getAllNews() {
    return newsRepo.findAll().stream().map(news -> new NewsGetResponse(
            news.getTitle(),
            news.getContent(),
            news.getLatitude(),
            news.getLongitude(),
            news.getDistrict(),
            news.getDate().toString()
    )).toList();
  }


  /**
   * Method to get news by district
   * @param district the district to get news from
   * @return List of NewsGetResponse
   */
  public List<NewsGetResponse> getByDistrict(String district) {
    return newsRepo.findByDistrict(district).stream()
            .map(news -> new NewsGetResponse(
                    news.getTitle(),
                    news.getContent(),
                    news.getLatitude(),
                    news.getLongitude(),
                    news.getDistrict(),
                    news.getDate().toString()
            )).toList();
  }


//  /**
//   * Method to add news to the database
//   * @param title the title of the news
//   * @param content the content of the news
//   * @param latitude the latitude of the news
//   * @param longitude the longitude of the news
//   * @param district the district of the news
//   */
//  public void addNews(String title, String content, double latitude, double longitude, String district) {
//    News news = new News(title, content, latitude, longitude, district, new Date());
//    newsRepo.save(news);
//  }
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
        String title = entry.getTitle();
        String content = entry.getDescription().getValue();
        Date publishedDate = entry.getPublishedDate();

        List<SyndCategory> categories = entry.getCategories();
        String district = categories.isEmpty() ? "Ukjent distrikt" : categories.get(0).getName();

        double lat = 0.0;
        double lon = 0.0;

        News news = new News(title, content, lat, lon, district, publishedDate);
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
