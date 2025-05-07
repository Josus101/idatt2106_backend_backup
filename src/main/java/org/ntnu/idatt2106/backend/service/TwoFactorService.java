package org.ntnu.idatt2106.backend.service;

import java.util.Date;
import java.util.Optional;
import org.ntnu.idatt2106.backend.model.VerificationToken;
import org.ntnu.idatt2106.backend.model.VerificationTokenType;
import org.ntnu.idatt2106.backend.repo.VerificationTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;


/**
 * Service class for handling two-factor authentication (2FA) tokens.
 * This service is responsible for generating, validating, and managing 2FA tokens.
 *
 * @Author Konrad Seime
 * @since 0.2
 */
@Service
public class TwoFactorService {

  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final SecureRandom RANDOM = new SecureRandom();

  @Autowired
  private VerificationTokenRepo verificationTokenRepo;

  /**
   * Verifies token not in use
   *
   * @param token The token to verify
   * @return true if the token is not in use, false otherwise
   */
  public boolean verifyTokenNotInUse(String token) {
    Optional<VerificationToken> verificationToken = verificationTokenRepo.findByToken(token);
    if (verificationToken.isEmpty()) {
      return true;
    }
    VerificationToken foundToken = verificationToken.get();
    if (foundToken.getType() == VerificationTokenType.TWO_FACTOR_AUTHENTICATION) {
      if (foundToken.getExpirationDate().before(new Date())) {
        verificationTokenRepo.delete(foundToken);
        return true;
      } else {
        return false;
      }
    }
    return false;
  }


  /**
   * Generates a 2FA token for the admin user.
   * This method should be called when the admin requests a new token.
   * The generated token should be sent to the user's email or phone number.
   */
  public String generate2FA_Token(int maxTries) {
    StringBuilder code = new StringBuilder(6);
    for (int j = 0; j < maxTries; j++) {
      for (int i = 0; i < 6; i++) {
        int index = RANDOM.nextInt(CHARACTERS.length());
        code.append(CHARACTERS.charAt(index));
      }
      if (verifyTokenNotInUse(code.toString())) {
        return code.toString();
      }
      code.setLength(0);
    }
    return null;
  }

  /**
   * Creates token for admin user and
   * saves the generated 2FA token to the database.
   * This method should be called after generating the token.
   *
   * @param email The email associated with the token.
   */
  public String create2FA_Token(String email) {

    String token = generate2FA_Token(10000);
    if (token == null) {
      throw new IllegalStateException("Failed to generate a unique token after multiple attempts.");
    }
    VerificationToken verificationToken = new VerificationToken(
        token,
        email,
        new java.util.Date(System.currentTimeMillis() + 15 * 60 * 1000),
        VerificationTokenType.TWO_FACTOR_AUTHENTICATION
    );
    verificationTokenRepo.save(verificationToken);
    return token;
  }
    /**
     * Validates the provided 2FA token against the stored token in the database.
     * This method should be called when the user submits the token for verification.
     *
     * @param token The token to validate.
     * @return true if the token is valid, false otherwise.
     */
  public boolean validate2FA_Token(String token) {
    Optional<VerificationToken> verificationToken = verificationTokenRepo.findByToken(token);
    if (verificationToken.isEmpty()) {
      return false;
    }
    if (!verificationToken.get().getType()
        .equals(VerificationTokenType.TWO_FACTOR_AUTHENTICATION)) {
      return false;
    }
    return !verificationToken.get().getExpirationDate().before(new java.util.Date());
  }

  /**
   * Gets Admin user by token.
   *
   * @param token The token to validate and extract admin from
   * @return The admin user associated with the token
   * @throws IllegalStateException if the token is invalid or the admin is not found
   * @throws IllegalArgumentException if the token is invalid or the admin is not found
   */
  public String getAdminUserByToken(String token) {
    Optional<VerificationToken> verificationToken = verificationTokenRepo.findByToken(token);
    if (verificationToken.isEmpty()) {
      throw new IllegalStateException("No admin found with given token");
    }
    if (!verificationToken.get().getType()
        .equals(VerificationTokenType.TWO_FACTOR_AUTHENTICATION)) {
      throw new IllegalArgumentException("Token is not a 2FA token");
    }
    return verificationToken.get().getEmail();
  }

  /**
   * Checks if the token belongs to the admin user.
   *
   * @param token The token to check.
   * @param admin The admin user.
   * @return true if the token belongs to the admin user, false otherwise.
   */
  public boolean isTokenForAdmin(String token, String admin) {
    Optional<VerificationToken> verificationToken = verificationTokenRepo.findByToken(token);
    if (verificationToken.isEmpty()) {
      return false;
    }
    if (!verificationToken.get().getType()
        .equals(VerificationTokenType.TWO_FACTOR_AUTHENTICATION)) {
      return false;
    }
    return verificationToken.get().getEmail().equals(admin);
  }

  /**
   * Deletes the 2FA token from the database after successful verification.
   * This method should be called after the token is used for authentication.
   *
   * @param token The token to delete.
   */
  public void delete2FA_Token(String token) {
    Optional<VerificationToken> verificationToken = verificationTokenRepo.findByToken(token);
    verificationToken.ifPresent(verificationTokenRepo::delete);
  }

}
