package org.ntnu.idatt2106.backend.dto.item;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Request object for creating an item")
public class ItemCreateRequest {
  @Schema(description = "Name of the item", example = "Water")
  private String name;
  @Schema(description = "Amount of the item", example = "10.0")
  private double amount;
  @Schema(description = "The category id of the item", example = "1")
  private int categoryId;
  @Schema(description = "The unit id of the item", example = "3")
    private int unitId;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @Schema(description = "Expiration date of the item", example = "2025-04-25")
  private Date expirationDate;
}
