package org.ntnu.idatt2106.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.ntnu.idatt2106.backend.dto.news.NewsCreateRequest;
import org.ntnu.idatt2106.backend.dto.news.NewsGetResponse;
import org.ntnu.idatt2106.backend.exceptions.AlreadyInUseException;
import org.ntnu.idatt2106.backend.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;
import java.util.List;

/**
 * Controller for the News model
 * @Author Jonas Reiher
 * @since 0.1
 * @version 0.1
 */
@RestController
@RequestMapping("/api/news")
public class NewsController {

  @Autowired
  private NewsService newsService;



  /**
   * Get all news from the database
   * @return List of NewsGetResponse
   */
  @GetMapping("/")
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
      )
  })
  public ResponseEntity<?> getNews() {
    try {
      return ResponseEntity.ok(newsService.getAllNews());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: No news found");
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
              schema = @Schema(example = "Error: Error retrieving news")
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
      System.out.println("Getting news for district: " + district);
      String fullDistrict = district + " Politidistrikt";
      System.out.println("Full district name: " + fullDistrict);
      List<NewsGetResponse> newsByDistrict = newsService.getByDistrict(fullDistrict);
      if (newsByDistrict.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: No news found for district: " + fullDistrict);
      }
      return ResponseEntity.ok(newsByDistrict);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Error: Error retrieving news");
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
          responseCode = "200",
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
  public ResponseEntity<?> addNews(@RequestBody NewsCreateRequest newsCreateRequest) {
    try {
      newsService.addNews(newsCreateRequest);
      return ResponseEntity.ok("News added successfully");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    } catch (AlreadyInUseException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: " + e.getMessage());
    }catch (Exception e) {
      return ResponseEntity.internalServerError().body("Error: Error adding news");
    }
  }
}
