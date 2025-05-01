package org.ntnu.idatt2106.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * News model for the database
 * @Author Jonas Reiher
 * @since 0.2
 * @version 0.2
 */
@Entity
@Table(name = "news")
@Getter
@Setter
@AllArgsConstructor
public class News {
  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  private int id;
  @Column(nullable = false)
  private String title;
  @Lob
  @Column(nullable = false)
  private String content;
  @Column(nullable = false)
  private double latitude;
  @Column(nullable = false)
  private double longitude;
  @Column(nullable = false)
  private String district;
  @Column(nullable = false)
  private Date date;

  /**
   * Blank constructor for the News model
   */
  public News() {}

  /**
   * Constructor for the News model
   * @param title of the news
   * @param content of the news
   * @param longitude of the news
   * @param latitude of the news
   * @param date of the news
   */
  public News(String title, String content, double latitude, double longitude, String district, Date date) {
    this.title = title;
    this.content = content;
    this.latitude = latitude;
    this.longitude = longitude;
    this.district = district;
    this.date = date;
  }

  @Override
  public String toString() {
    return "News{" +
            ", title='" + title + '\'' +
            ", content='" + content + '\'' +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            ", date=" + date +
            '}';
  }
}
