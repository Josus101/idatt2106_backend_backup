package org.ntnu.idatt2106.backend.service;


import java.util.Optional;
import org.ntnu.idatt2106.backend.exceptions.TokenExpiredException;
import org.ntnu.idatt2106.backend.exceptions.UserNotFoundException;
import org.ntnu.idatt2106.backend.model.EmailVerifyToken;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.repo.EmailVerificationTokenRepo;
import org.ntnu.idatt2106.backend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for verifying email addresses.
 *
 * @Author Konrad Seime
 * @since 0.1
 */
@Service
public class VerifyEmailService {

  @Autowired
  private EmailVerificationTokenRepo emailVerificationTokenRepo;

  @Autowired
  private UserRepo userRepo;

  @Autowired
  private LoginService loginService;

  /**
   * Finds the user associated with the provided token.
   */
  public User findUserByToken(String token) {
    Optional<EmailVerifyToken> emailVerificationToken = emailVerificationTokenRepo.findByToken(
        token);
    if (emailVerificationToken.isEmpty()) {
      throw new UserNotFoundException("User not found");
    }
    if (emailVerificationToken.get().getExpirationDate().before(new java.util.Date())) {
      throw new TokenExpiredException("Token expired");
    }
    return emailVerificationToken.map(EmailVerifyToken::getUser).orElse(null);
  }
  /**
   * Verifies the email address using the provided token.
   *
   * @param token The token to verify the email address.
   */
  public void verifyEmail(String token) {
    User user = findUserByToken(token);
    if (user != null) {
      loginService.verifyEmail(user);
    } else {
      throw new UserNotFoundException("User not found");
    }
  }
}
