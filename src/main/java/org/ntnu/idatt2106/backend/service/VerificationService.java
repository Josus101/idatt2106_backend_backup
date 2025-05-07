package org.ntnu.idatt2106.backend.service;


import java.util.Optional;
import org.ntnu.idatt2106.backend.exceptions.TokenExpiredException;
import org.ntnu.idatt2106.backend.exceptions.UserNotFoundException;
import org.ntnu.idatt2106.backend.model.Admin;
import org.ntnu.idatt2106.backend.model.VerificationToken;
import org.ntnu.idatt2106.backend.model.VerificationTokenType;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.repo.AdminRepo;
import org.ntnu.idatt2106.backend.repo.UserRepo;
import org.ntnu.idatt2106.backend.repo.VerificationTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for verifying email addresses.
 *
 * @Author Konrad Seime
 * @since 0.1
 */
@Service
public class VerificationService {

  @Autowired
  private VerificationTokenRepo verificationTokenRepo;

  @Autowired
  private UserRepo userRepo;

  @Autowired
  private LoginService loginService;

  @Autowired
  private AdminService adminService;

  @Autowired
  private AdminRepo adminRepo;

  /**
   * Finds email associated with the provided token.
   * @param token The token to find the user with
   * @param type The type of the token
   * @return The user with the given token
   * @throws UserNotFoundException if the user is not found
   * @throws TokenExpiredException if the token is expired
   */
  public String findEmailByToken(String token, VerificationTokenType type) {
    Optional<VerificationToken> emailVerificationToken = verificationTokenRepo.findByToken(
        token);
    if (emailVerificationToken.isEmpty()) {
      throw new UserNotFoundException("User not found");
    }
    if (!emailVerificationToken.get().getType()
        .equals(type)) {
      throw new IllegalArgumentException("Token is not a valid token of type " + type.name());
    }
    if (emailVerificationToken.get().getExpirationDate().before(new java.util.Date())) {
      throw new TokenExpiredException("Token expired");
    }
    return emailVerificationToken.get().getEmail();
  }

  /**
   * Finds the admin associated with the provided token.
   *
   * @param token The token to find the user with
   * @param type The type of the token
   * @return The user with the given token
   */
  public Admin findAdminByToken(String token, VerificationTokenType type) {
    String email = findEmailByToken(token, type);
    return adminRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Admin user not found"));
  }

  /**
   * Finds the user associated with the provided token.
   *
   * @param token The token to find the user with
   * @param type The type of the token
   * @return The user with the given token
   */
  public User findUserByToken(String token, VerificationTokenType type) {
    String email = findEmailByToken(token, type);
    return userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
  }
  /**
   * Verifies the email address using the provided token.
   *
   * @param token The token to verify the email address.
   * @throws UserNotFoundException if the user is not found.
   * @throws TokenExpiredException if the token is expired.
   */
  public void verifyEmail(String token) {
    try {
      User user = findUserByToken(token, VerificationTokenType.EMAIL_VERIFICATION);
      if (user != null) {
        loginService.verifyEmail(user);
      } else {
        throw new UserNotFoundException("User not found");
      }
    } catch (TokenExpiredException e) {
      throw new TokenExpiredException("Token expired");
    }
  }

  /**
   * Resets the password for the user with the given token
   * @param token The token to find the user with
   * @param newPassword The new password to set
   * @throws UserNotFoundException if the user is not found
   * @throws TokenExpiredException if the token is expired
   */
  public void resetPassword(String token, String newPassword) {
    User user = findUserByToken(token, VerificationTokenType.PASSWORD_RESET);
    if (user != null) {
      System.out.println("User found: " + user);
      loginService.resetPassword(user, newPassword);
    } else {
      throw new UserNotFoundException("User not found");
    }
  }

  /**
   * Activates the admin account using the provided token.
   *
   * @param token The token to activate the admin account.
   * @param newPassword The new password to set for the admin account.
   * @throws UserNotFoundException if the user is not found.
   */
  public void activateAdmin(String token, String newPassword) {
    Admin admin = findAdminByToken(token, VerificationTokenType.ADMIN_VERIFICATION);
    if (admin.isActive()) {
      throw new IllegalStateException("Admin account is already active");
    }
    System.out.println("admin found: " + admin);
    adminService.activateAdmin(admin, newPassword);
  }
}
