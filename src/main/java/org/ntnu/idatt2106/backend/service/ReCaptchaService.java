package org.ntnu.idatt2106.backend.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.ntnu.idatt2106.backend.dto.reCaptcha.ReCaptchaResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReCaptchaService {
  private static final String SECRET_KEY = "6Le5biErAAAAAAh1Md7I1y-EozvHCT-20zjE-i14";
  private static final String VERIFY_URL = "https://recaptchaenterprise.googleapis.com/v1/projects/systemutvikling2-1745329398192/assessments?key=" + SECRET_KEY;

  @Autowired
  private final RestTemplate restTemplate;

  /**
   * Constructor for the ReCaptchaService class
   * @param restTemplate the restTemplate used to handle sending post request to google services to verify a reCaptcha token
   */
  public ReCaptchaService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  /**
   * Validation method for the generated reCaptcha token
   * @param ReCaptchaToken the generated token
   * @return {@code true} if the token is valid, {@code false} otherwise
   */
  public boolean verifyReCaptchaToken(String ReCaptchaToken) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("secret", SECRET_KEY);
    params.add("response", ReCaptchaToken);

    ResponseEntity<ReCaptchaResponse> response = restTemplate.postForEntity(
            VERIFY_URL,
            params,
            ReCaptchaResponse.class
    );

    return response.getBody() != null && response.getBody().isSuccess();
  }
}
