package org.ntnu.idatt2106.backend.dto.household;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * A response data transfer object for a household status, with id-, name-, and status-of the household
 *
 * @author Jonas Reiher
 * @version 0.2
 * @since 0.2
 */
@Schema(description = "Response object for getting the status of a household")
@Getter
@Setter
@AllArgsConstructor
public class MyHouseholdStatusGetResponse {
  @Schema(description = "The Id of the household", example = "1")
  private int id;
  @Schema(description = "The name of the household", example = "My Home")
  private String name;
  @Schema(description = "The preparedness status of the household", implementation = PreparednessStatus.class)
  private PreparednessStatus status;
}
