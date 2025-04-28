package org.ntnu.idatt2106.backend.service;

import java.util.Optional;
import org.ntnu.idatt2106.backend.exceptions.UserNotFoundException;
import org.ntnu.idatt2106.backend.model.ResetPasswordToken;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.repo.ResetPasswordTokenRepo;
import org.ntnu.idatt2106.backend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for handling password reset functionality.
 * @Author Konrad Seime
 * @since 0.1
 */
@Service
public class ResetPasswordService {
  @Autowired
  private ResetPasswordTokenRepo resetPasswordTokenRepo;

  @Autowired
  private LoginService loginService;



  /**
   * Finds the user with the given token
   *
   * @param token The token to find the user with
   * @return The user with the given token
   */
  public User findUserByToken(String token) {
    Optional<ResetPasswordToken> resetPasswordToken = resetPasswordTokenRepo.findByToken(token);
    if (resetPasswordToken.isEmpty()) {
      throw new UserNotFoundException("User not found");
    }
    if (resetPasswordToken.get().getExpirationDate().before(new java.util.Date())) {
      throw new UserNotFoundException("Token expired");
    }
    return resetPasswordToken.map(ResetPasswordToken::getUser).orElse(null);
  }

  /**
   * Resets the password for the user with the given token
   * @param token The token to find the user with
   * @param newPassword The new password to set
   */
  public void resetPassword(String token, String newPassword) {
    User user = findUserByToken(token);
    if (user != null) {
      System.out.println("User found: " + user);
      loginService.resetPassword(user, newPassword);
    } else {
      throw new UserNotFoundException("User not found");
    }
  }

}
