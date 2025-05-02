package org.ntnu.idatt2106.backend.dto.item;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Data Transfer Object for an item.
 * This class is used to transfer item data between the client and server.
 * @Author Jonas Reiher
 * @since 0.1
 */
@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Response object for an item")
public class ItemGenericDTO {
  @Schema(description = "ID of the item", example = "1")
  private int id;
  @Schema(description = "Name of the item", example = "Water")
  private String name;
  @Schema(description = "Amount of the item", example = "10.0")
  private double amount;
  @Schema(description = "The unit id of the item", example = "3")
  private int unitId;
  @Schema(description = "The category id of the item", example = "1")
  private int categoryId;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @Schema(description = "Expiration date of the item", example = "2025-04-25")
  private Date expirationDate;
  @Schema(description = "The household ids of the item", example = "[1, 2, 3]")
  private List<Integer> householdIds;
}
