package org.ntnu.idatt2106.backend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "User position response DTO")
public class UserPositionResponse {
    @Schema(description = "latitude of user", example = "69.4")
    private double latitude;

    @Schema(description = "longitude of user", example = "18.9")
    private double longitude;

    @Schema(description = "time of last position update", example = "2023-10-01T12:00:00Z")
    private String positionUpdateTime;

    @Schema(description = "user id", example = "1")
    private int id;

    @Schema(description = "name of user", example = "Kuhn Aguero Agnes")
    private String name;


}
