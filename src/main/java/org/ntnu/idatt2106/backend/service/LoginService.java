package org.ntnu.idatt2106.backend.service;

import java.util.Optional;
import org.ntnu.idatt2106.backend.dto.user.UserRegisterRequest;
import org.ntnu.idatt2106.backend.dto.user.UserTokenResponse;
import org.ntnu.idatt2106.backend.exceptions.AlreadyInUseException;
import org.ntnu.idatt2106.backend.exceptions.MailSendingFailedException;
import org.ntnu.idatt2106.backend.exceptions.TokenExpiredException;
import org.ntnu.idatt2106.backend.exceptions.UserNotFoundException;
import org.ntnu.idatt2106.backend.exceptions.UserNotVerifiedException;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.security.BCryptHasher;
import org.ntnu.idatt2106.backend.security.JWT_token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

import org.ntnu.idatt2106.backend.repo.UserRepo;

/**
 * Service class responsible for login and user management operations.
 * This includes user authentication, registration, and updating profile data
 * @Author Konrad Seime
 * @since 0.1
 */
@Service
public class LoginService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JWT_token jwt;

    @Autowired
    private EmailService emailService;

    private final BCryptHasher hasher = new BCryptHasher();

    /**
     * Validates the format of an email address.
     *
     * @param email The email to validate.
     * @return true if the email format is valid, false otherwise.
     */
    public boolean validateEmail(String email) {
      return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    /**
     * Checks if the provided email is not already registered.
     *
     * @param email The email to check.
     * @return true if the email is available, false otherwise.
     */
    public boolean verifyEmailNotInUse(String email) {
      return userRepo.findByEmail(email).isEmpty();
    }

    /**
     * Validates that a password is not empty.
     *
     * @param password The password to validate.
     * @return true if the password is not empty, false otherwise.
     */
    public boolean validatePassword(String password) {
      return !password.isEmpty();
    }

    /**
     * Validates that the phone number is exactly 8 digits.
     *
     * @param phoneNumber The phone number to validate.
     * @return true if valid, false otherwise.
     */
    public boolean validatePhoneNumber(String phoneNumber) {
      return phoneNumber.matches("^\\d{8}$");
    }

    /**
     * Checks if the phone number is not already registered.
     *
     * @param phoneNumber The phone number to check.
     * @return true if the phone number is available, false otherwise.
     */
    public boolean verifyPhoneNumberNotInUse(String phoneNumber) {
      return userRepo.findByPhoneNumber(phoneNumber).isEmpty();
    }

    /**
     * Validates that the provided name contains only valid name characters.
     *
     * @param name The name to validate.
     * @return true if valid, false otherwise.
     */
    public boolean validateName(String name) {
      return name.matches("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$");
    }

    /**
     * Validates that all user information fields are valid.
     *
     * @param user The user to validate.
     * @return true if all fields are valid, false otherwise.
     */
    public boolean validateUser(User user) {
      return validateEmail(user.getEmail()) && validatePassword(user.getPassword()) && validatePhoneNumber(user.getPhoneNumber()) && validateName(user.getFirstname()) && validateName(user.getLastname());
    }

    /**
     * Authenticates a user using the provided email and password.
     * If the user is not verified, a verification email is sent if not already sent.
     *
     * @param email The user's email.
     * @param password The user's password.
     * @return A token response object on successful authentication.
     */
    public UserTokenResponse authenticate(String email, String password) throws IllegalArgumentException, UserNotVerifiedException, UserNotFoundException {
      Optional<User> user = userRepo.findByEmail(email);
      if (user.isEmpty()) {
        throw new UserNotFoundException("No user found with given email and password");
      }
      if (!user.get().isVerified()) {
        try {
          if (!emailService.hasValidVerificationEmail(user.get())) {
            emailService.sendVerificationEmail(user.get());
          }
          throw new UserNotVerifiedException("User is not verified");
        } catch (Exception e) {
          throw new MailSendingFailedException("Failed to send verification email", e.getCause());
        }
      }
      if (!hasher.checkPassword(password, user.get().getPassword())) {
        throw new IllegalArgumentException("Incorrect password for given email");
      }

      return jwt.generateJwtToken(user.get());
    }

    /**
     * Registers a new user if the email and phone number are not in use and the data is valid.
     *
     * @param userDTO The user to register.
     * @throws IllegalArgumentException if user data is invalid.
     */
    public void register(UserRegisterRequest userDTO) {
      if (!verifyEmailNotInUse(userDTO.getEmail())) {
        throw new AlreadyInUseException("Email is already in use");
      }
      if (!verifyPhoneNumberNotInUse(userDTO.getPhoneNumber())) {
        throw new AlreadyInUseException("Phone number is already in use");
      }
      User user = new User(
        userDTO.getEmail(),
        userDTO.getPassword(),
        userDTO.getFirstname(),
        userDTO.getLastname(),
        userDTO.getPhoneNumber()
      );

      if (!validateUser(user)) {
        throw new IllegalArgumentException("Invalid user data");
      }
      user.setPassword(hasher.hashPassword(user.getPassword()));
      try {
        emailService.sendVerificationEmail(user);
        userRepo.save(user);

      }catch (Exception e) {
        throw new MailSendingFailedException("Failed to send verification email", e.getCause());
      }
    }

  /**
   * Validates the given token and returns the associated user.
   *
   * @param token The JWT token to validate.
   * @return The user associated with the token.
   * @throws TokenExpiredException if the token has expired.
   */
  public User validateTokenAndGetUser(String token) {
    try {
      jwt.validateJwtToken(token);
      return jwt.getUserByToken(token);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid token");
    } catch (TokenExpiredException e) {
      throw new TokenExpiredException("Token has expired");
    } catch (Exception e) {
      throw new IllegalArgumentException("Error validating token");
    }
  }

  /**
   * Resets the password for the user
   * @param user The user to reset the password for
   * @param newPassword The new password to set
   */
  public void resetPassword(User user, String newPassword) {
    user.setPassword(hasher.hashPassword(newPassword));
    userRepo.save(user);
  }

  /**
   * Verifies the email address of the user.
   *
   * @param user The user to verify.
   */
  public void verifyEmail(User user) {
    user.setVerified(true);
    userRepo.save(user);
  }
}

