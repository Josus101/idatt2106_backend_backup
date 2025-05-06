package org.ntnu.idatt2106.backend.dto.household;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A response data transfer object for a household, with id and name of the household.
 *
 * @author Jonas Reiher
 * @version 0.2
 * @since 0.2
 */
@Schema(description = "Response object for a minimal household")
@Getter
@Setter
@AllArgsConstructor
@ToString
public class HouseholdMinimalGetResponse {
  @Schema(description = "The Id of the household", example = "1")
  private int id;
  @Schema(description = "The name of the household", example = "My Home")
  private String name;

}
