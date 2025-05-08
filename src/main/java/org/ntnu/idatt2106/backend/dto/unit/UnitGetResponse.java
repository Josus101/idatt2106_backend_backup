package org.ntnu.idatt2106.backend.dto.unit;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for getting a unit.
 * Contains the ID and name of the unit.
 * @Author Jonas Reiher
 * @since 0.1
 */
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Request object for getting a unit")
public class UnitGetResponse {
    @Schema(description = "ID of the unit", example = "1")
    private int id;
    @Schema(description = "English name of the unit", example = "PCS")
    private String englishName;
    @Schema(description = "Norwegian name of the unit", example = "Stk")
    private String norwegianName;
}
