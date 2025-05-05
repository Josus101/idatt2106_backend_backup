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
public class UserPositionUpdate {
    @Schema(description = "latitude of user", example = "69.4")
    private double latitude;

    @Schema(description = "longitude of user", example = "18.9")
    private double longitude;
}
