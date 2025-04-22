package org.ntnu.idatt2106.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserTokenDTO {
  private String token;
  private long expirationTime;

}
