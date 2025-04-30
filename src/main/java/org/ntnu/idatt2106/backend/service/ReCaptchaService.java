package org.ntnu.idatt2106.backend.service;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Service class for verifying reCAPTCHA tokens.
 */
@Service
public class ReCaptchaService {

  private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
  private final RestTemplate restTemplate;
  @Value("${recaptcha.secret-key}")
  private String secretKey;

  /**
   * Constructor for ReCaptchaService.
   *
   * @param restTemplate the RestTemplate to use for making HTTP requests
   */
  public ReCaptchaService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Verifies the reCAPTCHA token.
   *
   * @param token the reCAPTCHA token to verify
   * @return true if the token is valid, false otherwise
   */
  public boolean verifyToken(String token) {

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("secret", secretKey);
    body.add("response", token);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

    ResponseEntity<Map> response = restTemplate.postForEntity(VERIFY_URL, request, Map.class);
    Map<String, Object> responseBody = response.getBody();

    if (responseBody == null) {
      return false;
    }

    boolean success = (Boolean) responseBody.get("success");
    double score = (Double) responseBody.get("score");
    return success && score >= 0.5;
  }
}
