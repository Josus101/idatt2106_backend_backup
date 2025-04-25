package org.ntnu.idatt2106.backend.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for hashing and checking passwords using BCrypt.
 * It provides methods to hash a password and check if a given password matches a hashed password.
 * @Author Konrad Seime
 * @since 0.1
 */
@Component
public class BCryptHasher {

  private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

  private boolean stringEmpty(String password) {
    return password == null || password.isEmpty();
  }

  public String hashPassword(String password) {
    if (stringEmpty(password)) {
      return null;
    }
    return bCryptPasswordEncoder.encode(password);
  }

  public boolean checkPassword(String password, String hash) {
    if (stringEmpty(password) || stringEmpty(hash)) {
      return false;
    }
    return bCryptPasswordEncoder.matches(password, hash);
  }


}
