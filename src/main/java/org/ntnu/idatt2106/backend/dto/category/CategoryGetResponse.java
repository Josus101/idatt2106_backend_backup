package org.ntnu.idatt2106.backend.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Request object for getting a category")
public class CategoryGetResponse {
    @Schema(description = "ID of the category", example = "1")
    private int id;
    @Schema(description = "Name of the category", example = "Food")
    private String name;
}
