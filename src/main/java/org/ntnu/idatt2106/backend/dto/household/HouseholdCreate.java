package org.ntnu.idatt2106.backend.dto.household;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data transfer object for creating a household.
 * This class is used to transfer household data between the client and server.
 * It includes fields for household name, location (latitude and longitude).
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Data transfer object for creating a household")
public class HouseholdCreate {
    @Schema(description = "Name of the household", example = "My Household")
    private String name;
    @Schema(description = "Latitude of the household", example = "60.39299")
    private double latitude;
    @Schema(description = "Longitude of the household", example = "5.32415")
    private double longitude;

}
