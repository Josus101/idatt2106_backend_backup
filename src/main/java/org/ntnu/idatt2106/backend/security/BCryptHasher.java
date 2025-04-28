package org.ntnu.idatt2106.backend.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for hashing and checking passwords using BCrypt.
 * It provides methods to hash a password and check if a given password matches a hashed password.
 * @Author Konrad Seime, Jonas Reiher
 * @since 0.1
 */
@Component
public class BCryptHasher {

  private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

  /**
   * Checks if the given password is empty or null.
   *
   * @param password The password to check.
   * @return true if the password is empty or null, false otherwise.
   */
  private boolean stringEmpty(String password) {
    return password == null || password.isEmpty();
  }

  /**
   * Hashes the given password using BCrypt.
   *
   * @param password The password to hash.
   * @return The hashed password, or null if the input password is empty or null.
   */
  public String hashPassword(String password) {
    if (stringEmpty(password)) {
      return null;
    }
    return bCryptPasswordEncoder.encode(password);
  }

  /**
   * Checks if the given password matches the hashed password.
   *
   * @param password The password to check.
   * @param hash The hashed password to compare against.
   * @return true if the password matches the hash, false otherwise.
   */
  public boolean checkPassword(String password, String hash) {
    if (stringEmpty(password) || stringEmpty(hash)) {
      return false;
    }
    return bCryptPasswordEncoder.matches(password, hash);
  }

}
