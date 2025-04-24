package org.ntnu.idatt2106.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityConfigTestController {

  @GetMapping("/swagger-ui/index.html")
  public String swagger() {
    return "swagger OK";
  }

  @GetMapping("/secure")
  public String secure() {
    return "secure OK";
  }
}
