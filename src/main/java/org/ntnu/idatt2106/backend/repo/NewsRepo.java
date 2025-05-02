package org.ntnu.idatt2106.backend.repo;

import org.ntnu.idatt2106.backend.model.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Repository for the News model
 * @Author Jonas Reiher
 * @since 0.2
 * @version 0.2
 */
public interface NewsRepo extends JpaRepository<News, Integer> {

  /**
   * Finds a news article by its id.
   * @param id the id of the news article
   * @return the news article with the given id
   */
  Optional<News> findById(int id);

  /**
   * Finds a news article by its title.
   * @param title the title of the news article
   * @return the news article with the given title
   */
  Optional<News> findByTitle(String title);

  /**
   * Finds a news article by its title and date.
   * @param title the title of the news article
   * @param date the date of the news article
   * @return true if the news article exists, false otherwise
   */
  boolean existsByTitleAndDate(String title, Date date);

  /**
   * Finds all news articles
   * @return a list of news articles
   */
  @Override
  List<News> findAll();


  /**
   * Finds all news articles by district
   * @param district the district of the news article
   * @return a list of news articles in the given district
   */
  List<News> findByDistrict(String district);

  /**
   * Finds all news articles by case id
   * @param caseId the case id of the news article
   * @return a list of news articles in the given case id
   */
  List<News> findByCaseId(String caseId);

}
