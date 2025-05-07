package org.ntnu.idatt2106.backend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object used for email requests.
 *
 * @Author Konrad Seime
 * @since 0.2
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data transfer object for email request")
public class EmailRequest {
  @Schema(description = "email address", example = "ape@ape.no")
  private String email;

}
