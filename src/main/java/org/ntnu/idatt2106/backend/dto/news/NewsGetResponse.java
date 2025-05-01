package org.ntnu.idatt2106.backend.dto.news;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for the NewsGetResponse
 * @Author Jonas Reiher
 * @since 0.1
 * @version 0.1
 */
@Schema(description = "Request object for getting a news")
@Getter
@Setter
@AllArgsConstructor
@ToString
public class NewsGetResponse {
  @Schema(description = "The title of the news", example = "News title")
  private String title;
  @Schema(description = "The content of the news", example = "News content")
  private String content;
  @Schema(description = "The latitude of the news", example = "60.39299")
  private double latitude;
  @Schema(description = "The longitude of the news", example = "5.32415")
  private double longitude;
  @Schema(description = "The district of the news", example = "Oslo")
  private String district;
  @Schema(description = "The date of the news", example = "2025-04-30 12:59:03")
  private String date;
}
