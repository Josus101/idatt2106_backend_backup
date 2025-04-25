package org.ntnu.idatt2106.backend.dto.reCaptcha;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Response object for Google's reCAPTCHA verification.")
public class ReCaptchaResponse {

  @Schema(
          description = "True if the CAPTCHA was successfully validated by Google.",
          example = "true"
  )
  private boolean success;

  @Schema(
          description = "Timestamp of the challenge load (in ISO 8601 format).",
          example = "2025-04-23T12:34:56Z"
  )
  private String challenge_ts;

  @Schema(
          description = "The hostname of the site where the reCAPTCHA was solved.",
          example = "yourdomain.com"
  )
  private String hostname;

  @Schema(
          description = "Optional list of error codes if the CAPTCHA validation fails. " +
                  "Possible values include:\n" +
                  "- missing-input-secret: The secret parameter is missing.\n" +
                  "- invalid-input-secret: The secret parameter is invalid or malformed.\n" +
                  "- missing-input-response: The response parameter is missing.\n" +
                  "- invalid-input-response: The response parameter is invalid or malformed.\n" +
                  "- bad-request: The request is invalid or malformed.\n" +
                  "- timeout-or-duplicate: The response is no longer valid: either is too old or has been used previously.",
          example = "[\"invalid-input-response\"]"
  )
  private List<String> errorCodes;
}
