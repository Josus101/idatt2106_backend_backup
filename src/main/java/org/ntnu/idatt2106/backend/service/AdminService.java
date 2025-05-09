package org.ntnu.idatt2106.backend.service;

import jakarta.mail.MessagingException;
import java.util.List;
import java.util.Optional;

import org.ntnu.idatt2106.backend.dto.admin.AdminGetResponse;
import org.ntnu.idatt2106.backend.dto.admin.AdminLoginResponse;
import org.ntnu.idatt2106.backend.dto.user.UserAdminResponse;
import org.ntnu.idatt2106.backend.dto.user.UserMinimalGetResponse;
import org.ntnu.idatt2106.backend.exceptions.UnauthorizedException;
import org.ntnu.idatt2106.backend.exceptions.UserNotFoundException;
import org.ntnu.idatt2106.backend.exceptions.UserNotVerifiedException;
import org.ntnu.idatt2106.backend.model.Admin;
import org.ntnu.idatt2106.backend.model.User;
import org.ntnu.idatt2106.backend.repo.AdminRepo;
import org.ntnu.idatt2106.backend.repo.UserRepo;
import org.ntnu.idatt2106.backend.repo.UserSettingsRepo;
import org.ntnu.idatt2106.backend.repo.VerificationTokenRepo;
import org.ntnu.idatt2106.backend.security.BCryptHasher;
import org.ntnu.idatt2106.backend.security.JWT_token;
import org.ntnu.idatt2106.backend.service.TwoFactorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ntnu.idatt2106.backend.exceptions.MailSendingFailedException;

/**
 * Service class for handling admin-related operations.
 *
 * @Author Konrad Seime
 * @since 0.1
 */
@Service
public class AdminService {

  @Autowired
  private AdminRepo adminRepo;

  @Autowired
  private UserRepo userRepo;

  @Autowired
  private UserSettingsRepo userSettingsRepo;

  @Autowired
  private VerificationTokenRepo verificationTokenRepo;

  @Autowired
  private JWT_token jwt;

  @Autowired
  private EmailService emailService;

  @Autowired
  private TwoFactorService twoFactorService;

  private final BCryptHasher hasher = new BCryptHasher();


  /**
   * Validates that a password is not empty.
   *
   * @param password The password to validate.
   * @return true if the password is not empty, false otherwise.
   */
  public boolean validatePassword(String password) {
    return password.length() >= 8 && password.length() <= 50;
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
   * Checks if the provided username is not already registered.
   *
   * @param username The username to check.
   * @return true if the username is available, false otherwise.
   */
  public boolean verifyUsernameNotInUse(String username) {
    return !adminRepo.existsByUsername(username);
  }

  /**
   * Validates the admin user credentials.
   *
   * @param username The username to validate.
   * @param password The password to validate.
   * @return true if the credentials are valid, false otherwise.
   */
  public boolean validateAdminUser(String username, String password) {
    return validateName(username) && verifyUsernameNotInUse(username) && validatePassword(password);
  }

  /**
   * Gets admin user by token.
   *
   * @param token The token to validate and extract admin from
   * @throws IllegalArgumentException if the token is invalid or the admin is not found
   */
  private Admin getAdminUserByToken(String token) {
    return jwt.getAdminUserByToken(token);
  }

  /**
   * Retrieves the admin user from the authorization header.
   *
   * @param authorizationHeader The authorization header containing the bearer token.
   * @return The admin user associated with the token.
   * @throws IllegalArgumentException if the token is invalid or the admin is not found
   */
  private String getTokenFromAuthorizationHeader(String authorizationHeader) {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      throw new IllegalArgumentException("Invalid authorization header");
    }
    return authorizationHeader.substring(7);
  }

  /**
   * Verifies if the admin is a superuser.
   *
   * @param token The token to validate and check if the admin is a superuser.
   * @throws UnauthorizedException if the token is invalid or the admin is not a superuser
   */
  public void verifyAdminIsSuperUser(String token) {
    Admin admin = getAdminUserByToken(token);
    if (!admin.isSuperUser()) {
      throw new UnauthorizedException("Admin is not a super user");
    }
  }

  /**
   * Authenticates an admin user using the provided username and password.
   *
   * @param username The admin's username.
   * @param password The admin's password.
   * @return A token response object on successful authentication.
   */
  public AdminLoginResponse authenticate(String username, String password, String token) {
    Optional<Admin> admin = adminRepo.findByUsername(username);
    if (admin.isEmpty()) {
      throw new UserNotFoundException("No admin found with given username and password");
    }
    if (!admin.get().isActive()) {
      throw new UserNotVerifiedException("Admin is not active");
    }
    if (!hasher.checkPassword(password, admin.get().getPassword())) {
      throw new IllegalArgumentException("Incorrect password for given username");
    }
    if (!admin.get().isTwoFactorEnabled()) {
      if (admin.get().isSuperUser()) {
        return new AdminLoginResponse(jwt.generateJwtToken(admin.get()).getToken(), true);
      }
      return new AdminLoginResponse(jwt.generateJwtToken(admin.get()).getToken(), false);
    }
    if (token == null || token.isEmpty()) {
      throw new IllegalArgumentException("2FA token is required");
    }
    if (twoFactorService.isTokenForAdmin(token, admin.get().getEmail())) {
      if (!twoFactorService.validate2FA_Token(token)) {
        throw new IllegalArgumentException("Invalid 2FA token");
      }
      twoFactorService.delete2FA_Token(token);
    } else {
      throw new IllegalArgumentException("Invalid 2FA token");
    }

    if(admin.get().isSuperUser()) {
      return new AdminLoginResponse(jwt.generateJwtToken(admin.get()).getToken(), true);
    }

    return new AdminLoginResponse(jwt.generateJwtToken(admin.get()).getToken(), false);
  }

  /**
   * Authenticates an admin user using the provided username and password.
   *
   * @param username The admin's username.
   * @param password The admin's password.
   * @return A token response object on successful authentication.
   */
  public AdminLoginResponse authenticate(String username, String password) {
    return authenticate(username, password, null);
  }

  /**
   * Registers a new admin user if the username is not in use and the data is valid.
   * Registering a new admin requires superuser privileges, and will throw an exception if
   * the admin is not a superuser.
   *
   * @param admin The admin to register.
   * @throws IllegalArgumentException if admin data is invalid or username is already in use.
   * @throws UnauthorizedException if the admin is not authorized to register a new admin.
   */
  public void register(Admin admin, String header) {
    String token = getTokenFromAuthorizationHeader(header);
    try {
      verifyAdminIsSuperUser(token);
    } catch (UnauthorizedException e) {
      throw new UnauthorizedException("You are not authorized to register a new admin");
    }
    if (!verifyUsernameNotInUse(admin.getUsername())) {
      throw new IllegalArgumentException("Username is already in use");
    }
    if (!validateName(admin.getUsername()) || !verifyUsernameNotInUse(admin.getUsername())) {
      throw new IllegalArgumentException("Invalid admin data");
    }
    adminRepo.save(admin);
    sendActivateEmail(admin);
  }

  /**
   * Registers a new admin user with the provided username and password.
   *
   * @param username The admin's username.
   * @param token The token of the admin registering the new admin.
   * @throws IllegalArgumentException if admin data is invalid or username is already in use.
   */
  public void register(String username, String email, String token) {
    Admin admin = new Admin(username, "", email, false);
    register(admin, token);
  }

  /**
   * Elevates an admin user to a superuser.
   *
   * @param id The ID of the admin to elevate.
   * @param authorizationHeader The authorization header containing the
   *                            token of the admin performing the action.
   * @throws IllegalArgumentException if the admin is not found or is already a superuser.
   * @throws UnauthorizedException if the admin is not authorized to elevate another admin.
   */
  public void elevateAdmin(String id, String authorizationHeader) {
    String token = getTokenFromAuthorizationHeader(authorizationHeader);
    try {
      verifyAdminIsSuperUser(token);
    } catch (UnauthorizedException e) {
      throw new UnauthorizedException("You are not authorized to elevate an admin");
    }
    Optional<Admin> admin = adminRepo.findById(Integer.parseInt(id));
    if (admin.isEmpty()) {
      throw new IllegalArgumentException("No admin found with given id");
    }
    Admin adminUser = admin.get();
    if (adminUser.isSuperUser()) {
      throw new IllegalArgumentException("Admin is already a super user");
    }
    adminUser.setSuperUser(true);
    adminRepo.save(adminUser);
  }

  /**
   * Deletes an admin user.
   *
   * @param id The ID of the admin to delete.
   * @param authorizationHeader The authorization header containing the
   *                            token of the admin performing the action.
   * @throws IllegalArgumentException if the admin is not found or is a superuser.
   * @throws UnauthorizedException if the admin is not authorized to delete another admin.
   */
  public void exterminateAdmin(String id, String authorizationHeader) {
    String token = getTokenFromAuthorizationHeader(authorizationHeader);
    try {
      verifyAdminIsSuperUser(token);
    } catch (UnauthorizedException e) {
      throw new UnauthorizedException("You are not authorized to delete an admin");
    }
    Optional<Admin> admin = adminRepo.findById(Integer.parseInt(id));
    if (admin.isEmpty()) {
      throw new IllegalArgumentException("No admin found with given id");
    }
    Admin adminUser = admin.get();
    if (adminUser.isSuperUser()) {
      throw new IllegalArgumentException("Cannot delete a super user");
    }
    adminRepo.delete(adminUser);
  }

  /**
   * Retrieves all admin users.
   *
   * @param authorizationHeader The authorization header containing the bearer token.
   * @return A list of all admin users.
   */
  public List<AdminGetResponse> getAllAdmins(String authorizationHeader) {
    String token = getTokenFromAuthorizationHeader(authorizationHeader);
    try {
      verifyAdminIsSuperUser(token);
      List<AdminGetResponse> admins = adminRepo.findAll().stream().map(admin -> new AdminGetResponse(
          admin.getId(),
          admin.getUsername(),
          admin.getEmail(),
          admin.isSuperUser()
      )).toList();
      if (admins.isEmpty()) {
        throw new UserNotFoundException("No admins found");
      }
      return admins;
    } catch (UnauthorizedException e) {
      throw new UnauthorizedException("You are not authorized to get all admins");
    }
  }

  /**
   * Changes password for the admin user.
   *
   * @param admin The admin user.
   * @param newPassword The new password.
   * @throws IllegalArgumentException if the password is invalid.
   *
   */
  public void changePassword(Admin admin, String newPassword) {
    if (!validatePassword(newPassword)) {
      throw new IllegalArgumentException("Invalid password");
    }
    admin.setPassword(hasher.hashPassword(newPassword));
    if (!admin.isActive()) {
      admin.setActive(true);
    }
    adminRepo.save(admin);
  }

  /**
   * Send email to activate the admin user.
   *
   * @param admin The admin user.
   * @throws IllegalArgumentException if the admin is already active.
   * @throws MailSendingFailedException if the email sending fails.
   */
  public void sendActivateEmail(Admin admin) {
    try {
      if (admin.isActive()) {
        throw new IllegalArgumentException("Admin is already active");
      }
      emailService.sendAdminActivationEmail(admin);
    } catch (Exception e) {
      throw new MailSendingFailedException("Failed to send activation email", e.getCause());
    }

  }

  /**
   * Activates the admin user.
   *
   * @param admin The admin user.
   * @param newPassword The new password.
   * @throws IllegalArgumentException if the token is invalid or the admin is already active.
   */
  public void activateAdmin(Admin admin, String newPassword) {
    if (admin.isActive()) {
      throw new IllegalArgumentException("Admin is already active");
    }
    admin.setActive(true);
    changePassword(admin, newPassword);
  }

  /**
   * Sends a 2FA token to the admin user.
   *
   * @param admin The admin user.
   */
  public void send2FAToken(Admin admin) {
    try {
      if (!admin.isTwoFactorEnabled()) {
        throw new IllegalArgumentException("2FA is not enabled for this admin");
      }
      if (!admin.isActive()) {
        throw new UserNotVerifiedException("Admin is not active");
      }
      String token = twoFactorService.create2FA_Token(admin.getEmail());
      emailService.send2FA(admin.getEmail(), token);
    } catch (UserNotFoundException e) {
      throw new UserNotFoundException("Admin not found");
    } catch (UserNotVerifiedException e) {
      throw new UserNotVerifiedException("Admin is not active");
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("2FA is not enabled for this admin");
    } catch (Exception e) {
      throw new MailSendingFailedException("Failed to send 2FA token", e.getCause());
    }
  }

  /**
   * Sends a 2FA token to the admin user.
   *
   * @param username The email of the admin user.
   */
  public void send2FAToken(String username) {
    try {
      if (username == null || username.isEmpty()) {
        throw new IllegalArgumentException("Username is required");
      }
      Admin admin = adminRepo.findByUsername(username)
          .orElseThrow(() -> new UserNotFoundException("Admin not found"));
      send2FAToken(admin);
    } catch (UserNotFoundException e) {
      throw new UserNotFoundException("Admin not found");
    } catch (UserNotVerifiedException e) {
      throw new UserNotVerifiedException("Admin is not active");
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("2FA is not enabled for this admin");
    } catch (Exception e) {
      throw new MailSendingFailedException("Failed to send 2FA token", e.getCause());
    }
  }

  /**
   * Verifies the admin user from the provided token.
   *
   * @param token The token to verify.
   * @throws IllegalArgumentException if the token is invalid or the admin is not found.
   */
  public void verifyAdminFromToken(String token) {
    if (token == null || token.isEmpty()) {
      throw new IllegalArgumentException("Token is required");
    }
    Admin admin = jwt.getAdminUserByToken(token);
    if (admin == null) {
      throw new UnauthorizedException("Invalid token");
    }
  }

  /**
   * Retrieves all users.
   *
   * @param authorizationHeader The authorization header containing the bearer token.
   * @return A list of all users.
   */
  public List<UserAdminResponse> getAllUsers(String authorizationHeader) {
    String token = getTokenFromAuthorizationHeader(authorizationHeader);
    try {
      verifyAdminFromToken(token);
      List<UserAdminResponse> users = userRepo.findAll().stream().map(user -> new UserAdminResponse(
          user.getId(),
          user.getFirstname() +" "+ user.getLastname(),
          user.getEmail(),
          user.getPhoneNumber(),
          user.getHouseholdMembershipsString()
      )).toList();
      if (users.isEmpty()) {
        throw new UserNotFoundException("No users found");
      }
      return users;
    } catch (UnauthorizedException e) {
      throw new UnauthorizedException("You are not authorized to get all users");
    }
  }

  /**
   * Deletes a user by id
   *
   * @param id The id of the user to delete
   * @param authorizationHeader The authorization header containing the bearer token.
   * @throws IllegalArgumentException if the user is not found.
   * @throws UnauthorizedException if the admin is not authorized to delete a user.
   */
  public void deleteUser(String id, String authorizationHeader) {
    String token = getTokenFromAuthorizationHeader(authorizationHeader);
    try {
      verifyAdminFromToken(token);
      Optional<User> user = userRepo.findById(Integer.parseInt(id));
      if (user.isEmpty()) {
        throw new IllegalArgumentException("No user found with given id");
      }
      if (user.get().getHouseholdMemberships() != null) {
        user.get().getHouseholdMemberships().forEach(hm -> {
          hm.getHousehold().removeMember(user.get());
        });
      }
      if (user.get().getUserSettings() != null) {
        userSettingsRepo.delete(user.get().getUserSettings());
      }
      userRepo.delete(user.get());
    } catch (UnauthorizedException e) {
      throw new UnauthorizedException("You are not authorized to delete a user");
    }
  }

  /**
   * Sends a password reset email to the user.
   *
   * @param email The email of the user.
   * @param authorizationHeader The authorization header containing the bearer token.
   * @throws MessagingException if the email sending fails.
   */
  public void sendPasswordResetEmail(String email, String authorizationHeader)
      throws MessagingException {
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      throw new IllegalArgumentException("Invalid authorization header");
    }
    verifyAdminIsSuperUser(authorizationHeader.substring(7));
    if (email == null || email.isEmpty()) {
      throw new IllegalArgumentException("Email is required");
    }
    Admin admin = adminRepo.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("Admin not found"));
    emailService.sendAdminPasswordResetEmail(admin);
  }
}