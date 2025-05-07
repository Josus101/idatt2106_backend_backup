package org.ntnu.idatt2106.backend.controller;

import org.springframework.http.ResponseEntity;

@FunctionalInterface
public interface TokenRequestHandler {
  ResponseEntity<?> apply(Object... args);
}

