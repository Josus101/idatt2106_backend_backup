package org.ntnu.idatt2106.backend.dto.household;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data transfer object for household requests")
public class HouseholdRequest {
    @Schema(description = "ID of the household", example = "12345")
    private int id;

    @Schema(description = "Name of the household", example = "My Household")
    private String name;

    @Schema(description = "Latitude of the household", example = "60.39299")
    private double latitude;

    @Schema(description = "Longitude of the household", example = "5.32415")
    private double longitude;

    @Schema(description = "Members of the household", example = "kåre, kakkel, ovn")
    private List<String> members;

    @Schema(description = "Inventory of the household", example = "gun, halberd, your mom's sword, your mom's axe")
    private List<String> inventory;



}
