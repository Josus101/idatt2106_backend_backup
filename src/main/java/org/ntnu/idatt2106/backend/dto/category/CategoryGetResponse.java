package org.ntnu.idatt2106.backend.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for getting a category.
 * Contains the ID and name of the category.
 * @Author Jonas Reiher
 * @since 0.1
 */
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Request object for getting a category")
public class CategoryGetResponse {
    @Schema(description = "ID of the category", example = "1")
    private int id;
    @Schema(description = "English name of the category", example = "Food")
    private String englishName;
    @Schema(description = "Norwegian name of the category", example = "Mat")
    private String norwegianName;
}
