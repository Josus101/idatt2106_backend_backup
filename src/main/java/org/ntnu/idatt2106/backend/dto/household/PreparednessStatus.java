package org.ntnu.idatt2106.backend.dto.household;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object representing the preparedness level of a household.
 * This class is used to transfer preparedness data between the client and server.
 * @Author Erlend Eide Zindel
 * @since 0.1
 */
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Data Transfer Object representing the preparedness level of a household")
public class PreparednessStatus {
    @Schema(description = "Preparedness percentage (0 to 100)", example = "75")
    private int preparednessPercent;

    @Schema(description = "Whether a warning should be shown (e.g. low supply or missing essentials)", example = "true")
    private boolean isWarning;

    @Schema(description = "A message explaining the preparedness status", example = "Lageret ditt dekker minst 7 dager")
    private String message;
}
