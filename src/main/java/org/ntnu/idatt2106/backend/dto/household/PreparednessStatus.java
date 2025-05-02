package org.ntnu.idatt2106.backend.dto.household;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object representing the preparedness level of a household.
 * This class is used to transfer preparedness data between the client and server.
 * @author Erlend Eide Zindel
 * @since 0.1
 * @version 0.2
 */
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Data Transfer Object representing the preparedness level of a household")
public class PreparednessStatus {
    @Schema(description = "How many days of food the storage covers for the household", example = "5")
    private double daysOfFood;

    @Schema(description = "How many days of water the storage covers for the household", example = "4")
    private double daysOfWater;
}
