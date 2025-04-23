package org.ntnu.idatt2106.backend.service;

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
  private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
  private static final String SECRET_KEY = "6Le5biErAAAAAAh1Md7I1y-EozvHCT-20zjE-i14";


  /**
   * Validation method for the generated reCaptcha token
   * @param ReCaptchaToken the generated token
   * @return {@code true} if the token is valid, {@code false} otherwise
   */
  public boolean verifyReCaptchaToken(String ReCaptchaToken) {
    RestTemplate restTemplate = new RestTemplate();

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("secret", SECRET_KEY);
    params.add("response", ReCaptchaToken);

    ResponseEntity<ReCaptchaResponse> response = restTemplate.postForEntity(
            VERIFY_URL,
            params,
            ReCaptchaResponse.class
    );

    response.getBody();
    return response.getBody().isSuccess();
  }
}
