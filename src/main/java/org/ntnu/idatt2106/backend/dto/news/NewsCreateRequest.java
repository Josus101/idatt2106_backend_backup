package org.ntnu.idatt2106.backend.dto.news;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for the NewsCreateRequest
 * @Author Jonas Reiher
 * @since 0.2
 * @version 0.2
 */
@Schema(description = "Request body for creating a news article")
@Getter
@Setter
@AllArgsConstructor
public class NewsCreateRequest {
  @Schema(description = "The title of the news", example = "News title")
  private String title;
  @Schema(description = "The case id of the news", example = "25h7fg")
  private String caseId;
  @Schema(description = "The content of the news", example = "News content")
  private String content;
  @Schema(description = "The latitude of the news", example = "60.39299")
  private double latitude;
  @Schema(description = "The longitude of the news", example = "5.32415")
  private double longitude;
  @Schema(description = "The district of the news", example = "Oslo")
  private String district;

}
