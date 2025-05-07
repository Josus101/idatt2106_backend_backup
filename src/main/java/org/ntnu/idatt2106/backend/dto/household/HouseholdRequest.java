package org.ntnu.idatt2106.backend.dto.household;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.ntnu.idatt2106.backend.dto.user.UserMinimalGetResponse;

/**
 * Data transfer object for household requests.
 * This class is used to transfer household data between the client and server.
 * It includes fields for household ID, name, location (latitude and longitude),
 * members, and inventory.
 */
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

    @ArraySchema(schema = @Schema(
            description = "List of members in the household",
            implementation = UserMinimalGetResponse.class)
    )
    private List<UserMinimalGetResponse> members;

    @Schema(description = "Inventory of the household", example = "gun, halberd, your mom's sword, your mom's axe")
    private List<String> inventory;



}
