package org.ntnu.idatt2106.backend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for user store settings request.
 * This class is used to represent the request object for updating user store settings.
 */
@Schema(description = "Request object for updating user store settings")
@Getter
@Setter
@AllArgsConstructor
public class UserStoreSettingsRequest {
  @Schema(description = "Enable notifications", example = "true")
  private boolean enableNotifications;
  @Schema(description = "Enable location sharing", example = "false")
  private boolean enableLocationSharing;
  @Schema(description = "Enable storage status on frontpage", example = "true")
  private boolean showStorageStatusOnFrontpage;
  @Schema(description = "Enable household status on frontpage", example = "true")
  private boolean showHouseholdStatusOnFrontpage;
}
