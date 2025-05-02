package org.ntnu.idatt2106.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.ntnu.idatt2106.backend.dto.news.NewsCreateRequest;
import org.ntnu.idatt2106.backend.dto.news.NewsGetResponse;
import org.ntnu.idatt2106.backend.exceptions.AlreadyInUseException;
import org.ntnu.idatt2106.backend.security.JWT_token;
import org.ntnu.idatt2106.backend.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for the News model
 * @Author Jonas Reiher
 * @since 0.2
 * @version 0.3
 */
@RestController
@RequestMapping("/api/news")
public class NewsController {

  @Autowired
  private NewsService newsService;


  @Autowired
  private JWT_token jwt;

  /**
   * Get all news from the database
   * @return List of NewsGetResponse
   */
  @GetMapping("")
  @Operation(
      summary = "Get all news"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "News retrieved successfully",
          content = @Content(
              mediaType = "application/json",
              array = @ArraySchema(
                  schema = @Schema(implementation = NewsGetResponse.class)
              )
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Error: No news found",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: No news found")
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Error: Error retrieving news",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Error retrieving news")
          )
      )
  })
  public ResponseEntity<?> getNews() {
    try {
      List<NewsGetResponse> news = newsService.getAllNews(); // all the news
      List<List<NewsGetResponse>> groupedNews = newsService.groupNewsByCaseIdAndSort(news); // group the news by case
      List<NewsGetResponse> recentNews = newsService.getRecentFromGroupedNews(groupedNews); // get the most recent news from each case

      return ResponseEntity.status(HttpStatus.OK).body(recentNews);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Error retrieving news");
    }
  }

  /**
   * Retrieve news from the API feed
   * @return ResponseEntity with status code
   */
  @GetMapping("/retrieve")
  @Operation(
      summary = "Retrieve news from API feed"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "News retrieved successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "News retrieved successfully")
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Error retrieving news",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Error retrieving news")
          )
      )
  })
  public ResponseEntity<?> retrieveNews() {
    try {
      newsService.retrieveNewsFromAPIFeed();

      return ResponseEntity.ok("News retrieved successfully");
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Error: Error retrieving news");
    }
  }

  /**
   * Get all news from the database with the given district
   * @param district the district to get news from
   * @return List of NewsGetResponse
   */
  @GetMapping("/district/{district}")
  @Operation(
          summary = "Get news by district"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "News retrieved successfully",
          content = @Content(
              mediaType = "application/json",
              array = @ArraySchema(
                  schema = @Schema(implementation = NewsGetResponse.class)
              )
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Error: No news found for district",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: No news found for district")
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Error: Error retrieving news",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Error retrieving news for <district name>")
          )
      )
  })
  public ResponseEntity<?> getByDistrict(
          @Parameter(
              description = "District to get news from",
              required = true,
              examples = {
                  @ExampleObject(name = "Oslo",             value = "Oslo"),
                  @ExampleObject(name = "Øst",              value = "Øst"),
                  @ExampleObject(name = "Innlandet",        value = "Innlandet"),
                  @ExampleObject(name = "Sør-Øst",          value = "Sør-Øst"),
                  @ExampleObject(name = "Agder",            value = "Agder"),
                  @ExampleObject(name = "Sør-Vest",         value = "Sør-Vest"),
                  @ExampleObject(name = "Vest",             value = "Vest"),
                  @ExampleObject(name = "Møre og Romsdal",  value = "Møre og Romsdal"),
                  @ExampleObject(name = "Trøndelag",        value = "Trøndelag"),
                  @ExampleObject(name = "Nordland",         value = "Nordland"),
                  @ExampleObject(name = "Troms",            value = "Troms"),
                  @ExampleObject(name = "Finnmark",         value = "Finnmark")
              }
          ) @PathVariable String district
  ){
    try {
      String fullDistrict = district + " Politidistrikt";
      List<NewsGetResponse> newsByDistrict = newsService.getByDistrict(fullDistrict);
      List<List<NewsGetResponse>> groupedNews = newsService.groupNewsByCaseIdAndSort(newsByDistrict);

      List<NewsGetResponse> recentNews = newsService.getRecentFromGroupedNews(groupedNews);

      return ResponseEntity.status(HttpStatus.OK).body(recentNews);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Error retrieving news for district: " + district);
    }
  }

  /**
   * Get all news from the database with the given case id
   * @param caseId the case id to get news from
   * @return List of NewsGetResponse
   */
  @GetMapping("/{caseId}")
  @Operation(
      summary = "Get news by case ID"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "News retrieved successfully",
          content = @Content(
              mediaType = "application/json",
              array = @ArraySchema(
                  schema = @Schema(implementation = NewsGetResponse.class)
              )
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Error: No news found for case ID",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: No news found for case ID: <caseId>")
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Error: Error retrieving news",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Error retrieving news for case ID: <caseId>")
          )
      )
  })
  public ResponseEntity<?> getByCaseId(
          @Parameter(
              description = "Case ID to get news from",
              required = true,
              example = "25h7fg"
          ) @PathVariable String caseId
  ){
    try {
      List<NewsGetResponse> newsByCaseId = newsService.getByCaseId(caseId);

      return ResponseEntity.status(HttpStatus.OK).body(newsByCaseId);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Error retrieving news for case ID: " + caseId);
    }
  }

  /**
   * Add news to the database
   * @param newsCreateRequest the news to add
   * @return ResponseEntity with status code
   */
  @PostMapping("/add")
  @Operation(
      summary = "Add news to the database"
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "News added successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "News added successfully")
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Error: Bad request",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Bad request")
          )
      ),
      @ApiResponse(
              responseCode = "401",
              description = "Unauthorized access, must be admin user",
              content = @Content(
                      mediaType = "application/json",
                      schema = @Schema(example = "Error: Unauthorized")
              )
      ),
      @ApiResponse(
          responseCode = "409",
          description = "Error: News already exists",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: News already exists")
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Error adding news",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(example = "Error: Error adding news")
          )
      )
  })
  public ResponseEntity<?> addNews(
          @Parameter(
                  name = "Authorization",
                  description = "Bearer token in the format `Bearer <JWT>`",
                  required = true,
                  example = "Bearer eyJhbGciOiJIUzI1N.iIsInR5cCI6IkpXVCJ9..."
          ) @RequestHeader("Authorization") String authorizationHeader,
          @RequestBody NewsCreateRequest newsCreateRequest
  ){
    try {
      if (jwt.getAdminUserByToken(authorizationHeader) == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Unauthorized");
      }
      newsService.addNews(newsCreateRequest);

      return ResponseEntity.status(HttpStatus.CREATED).body("News added successfully");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    } catch (AlreadyInUseException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: " + e.getMessage());
    }catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Error adding news");
    }
  }
}
