package org.ntnu.idatt2106.backend.dto.household;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema(description = "Data Transfer Object for unregistered members request")
public class UnregisteredMembersRequest {
  @Schema(description = "The number of unregistered adults in the household", example = "2")
  private int unregisteredAdultCount;

  @Schema(description = "The number of unregistered children in the household", example = "1")
  private int unregisteredChildCount;

  @Schema(description = "The number of unregistered pets in the household", example = "3")
  private int unregisteredPetCount;
}
