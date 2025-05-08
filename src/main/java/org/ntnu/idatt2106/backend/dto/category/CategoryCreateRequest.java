package org.ntnu.idatt2106.backend.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request object for creating a new category.
 *
 * @Author Konrad Seime
 * @version 0.2
 * @since 0.2
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Request object for creating a new category")
public class CategoryCreateRequest {
  @Schema(description = "Name of the category", example = "Theft")
  private String name;

  @Schema(description = "kcalPerUnit of the category", example = "100")
  private Integer kcalPerUnit;

  @Schema(description = "Whether the category is essential", example = "true")
  private Boolean isEssential;

}
